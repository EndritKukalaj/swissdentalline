package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.EinnahmenProMonatDTO;
import ch.zhaw.swissdentalline.dto.TerminCreateDTO;
import ch.zhaw.swissdentalline.dto.TerminStatusAggregationDTO;
import ch.zhaw.swissdentalline.mapper.TerminMapper;
import ch.zhaw.swissdentalline.model.Termin;
import ch.zhaw.swissdentalline.model.TerminStatus;
import ch.zhaw.swissdentalline.repositories.BehandlungsartRepository;
import ch.zhaw.swissdentalline.repositories.PatientRepository;
import ch.zhaw.swissdentalline.repositories.TerminRepository;
import ch.zhaw.swissdentalline.repositories.ZahnarztRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TerminService {

    @Autowired
    private TerminRepository terminRepository;
    @Autowired
    private TerminMapper terminMapper;
    @Autowired
    private ZahnarztRepository zahnarztRepository;
    @Autowired
    private BehandlungsartRepository behandlungsartRepository;
    @Autowired
    private PatientRepository patientRepository;

    public Termin createTermin(TerminCreateDTO createDTO) {
        // Validate foreign keys
        if (!zahnarztRepository.existsById(createDTO.getZahnarztId())) {
            throw new IllegalArgumentException("Zahnarzt mit id: " + createDTO.getZahnarztId() + " nicht gefunden");
        }
        if (!behandlungsartRepository.existsById(createDTO.getBehandlungsartId())) {
            throw new IllegalArgumentException("Behandlungsart mit id: " + createDTO.getBehandlungsartId() + " nicht gefunden");
        }
        if (createDTO.getPatientId() != null && !patientRepository.existsById(createDTO.getPatientId())) {
            throw new IllegalArgumentException("Patient mit id: " + createDTO.getPatientId() + " nicht gefunden");
        }

        // Check for patient appointment overlap if patient is assigned
        if (createDTO.getPatientId() != null) {
            checkPatientOverlap(createDTO.getPatientId(), createDTO.getDatum(), createDTO.getDauerMinuten(), null);
        }

        Termin termin = terminMapper.toEntity(createDTO);
        return terminRepository.save(termin);
    }

    private void checkPatientOverlap(String patientId, Instant datum, Integer dauerMinuten, String excludeTerminId) {
        Instant terminStart = datum;
        Instant terminEnd = datum.plusSeconds(dauerMinuten * 60L);

        // Query for overlapping appointments (expand search window to catch edge cases)
        List<Termin> overlapping = terminRepository.findByPatientIdAndDatumBetween(
                patientId,
                terminStart.minusSeconds(24 * 3600), // 1 day before
                terminEnd.plusSeconds(24 * 3600)     // 1 day after
        );

        for (Termin existing : overlapping) {
            // Skip self when updating
            if (excludeTerminId != null && existing.getId().equals(excludeTerminId)) {
                continue;
            }

            Instant existingStart = existing.getDatum();
            Instant existingEnd = existingStart.plusSeconds(existing.getDauerMinuten() * 60L);

            // Check for actual overlap: new starts before existing ends AND new ends after existing starts
            if (terminStart.isBefore(existingEnd) && terminEnd.isAfter(existingStart)) {
                throw new IllegalStateException(
                        "Patient hat bereits einen Termin am " + existing.getDatum() + 
                        " der sich mit dem neuen Termin überschneidet"
                );
            }
        }
    }

    public Optional<Termin> getTerminById(String id) {
        return terminRepository.findById(id);
    }

    public List<Termin> getAllTermine() {
        return terminRepository.findAll();
    }

    public Page<Termin> findTermineByStatusAndDateRange(
            TerminStatus status, Instant start, Instant end, Pageable pageable) {
        return terminRepository.findByStatusAndDatumBetween(status, start, end, pageable);
    }

    public Page<Termin> searchAvailableTermine(Pageable pageable) {
        return terminRepository.findByStatus(TerminStatus.FREI, pageable);
    }

    public Page<Termin> findFlexTermine(Pageable pageable) {
        return terminRepository.findByStatus(TerminStatus.ABGESAGT, pageable);
    }

    public List<TerminStatusAggregationDTO> getTerminStateAggregation(String zahnarztId) {
        return terminRepository.getTerminStateAggregation(zahnarztId);
    }

    public List<EinnahmenProMonatDTO> getEinnahmenProMonatByZahnarzt(String zahnarztId, Instant start, Instant end) {
        return terminRepository.getEinnahmenProMonatById(zahnarztId, start, end);
    }

    public Termin updateTermin(String id, TerminCreateDTO updateDTO) {
        Termin existing = terminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Termin mit id: " + id + " nicht gefunden"));

        // Validate foreign keys
        if (!zahnarztRepository.existsById(updateDTO.getZahnarztId())) {
            throw new IllegalArgumentException("Zahnarzt mit id: " + updateDTO.getZahnarztId() + " nicht gefunden");
        }
        if (!behandlungsartRepository.existsById(updateDTO.getBehandlungsartId())) {
            throw new IllegalArgumentException("Behandlungsart mit id: " + updateDTO.getBehandlungsartId() + " nicht gefunden");
        }
        if (updateDTO.getPatientId() != null && !patientRepository.existsById(updateDTO.getPatientId())) {
            throw new IllegalArgumentException("Patient mit id: " + updateDTO.getPatientId() + " nicht gefunden");
        }

        // Check overlap if patient or date/duration changed
        if (updateDTO.getPatientId() != null) {
            checkPatientOverlap(updateDTO.getPatientId(), updateDTO.getDatum(), updateDTO.getDauerMinuten(), id);
        }

        existing.setZahnarztId(updateDTO.getZahnarztId());
        existing.setPatientId(updateDTO.getPatientId());
        existing.setBehandlungsartId(updateDTO.getBehandlungsartId());
        existing.setDatum(updateDTO.getDatum());
        existing.setDauerMinuten(updateDTO.getDauerMinuten());
        existing.setPreis(updateDTO.getPreis());
        existing.setStatus(updateDTO.getStatus());
        existing.setWartelisteAktiv(updateDTO.isWartelisteAktiv());

        return terminRepository.save(existing);
    }

    public void deleteTermin(String id) {
        if (!terminRepository.existsById(id)) {
            throw new IllegalArgumentException("Termin mit id: " + id + " nicht gefunden");
        }
        terminRepository.deleteById(id);
    }

    public Termin bookTermin(String terminId, boolean isWarteListe, String patientId) {
        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new IllegalArgumentException("Termin mit id: " + terminId + " nicht gefunden"));

        if (termin.getStatus() != TerminStatus.FREI) {
            throw new IllegalStateException("Termin ist nicht verfügbar");
        }

        // Validate patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new IllegalArgumentException("Patient mit id: " + patientId + " nicht gefunden");
        }

        // Check overlap
        checkPatientOverlap(patientId, termin.getDatum(), termin.getDauerMinuten(), terminId);

        termin.setPatientId(patientId);
        termin.setStatus(TerminStatus.GEBUCHT);
        termin.setWartelisteAktiv(isWarteListe);

        return terminRepository.save(termin);
    }

    public Termin cancelTermin(String terminId) {
        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new IllegalArgumentException("Termin mit id: " + terminId + " nicht gefunden"));

        termin.setPatientId(null);
        termin.setStatus(TerminStatus.ABGESAGT);

        return terminRepository.save(termin);
    }

    public Termin completeTermin(String terminId) {
        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new IllegalArgumentException("Termin mit id: " + terminId + " nicht gefunden"));

        if (termin.getStatus() != TerminStatus.GEBUCHT) {
            throw new IllegalStateException("Nur gebuchte Termine können abgeschlossen werden");
        }

        termin.setStatus(TerminStatus.ABGESCHLOSSEN);

        return terminRepository.save(termin);
    }
}
