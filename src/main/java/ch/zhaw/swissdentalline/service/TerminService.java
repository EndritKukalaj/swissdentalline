package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.EinnahmenProMonatDTO;
import ch.zhaw.swissdentalline.dto.TerminCreateDTO;
import ch.zhaw.swissdentalline.dto.TerminStatusAggregationDTO;
import ch.zhaw.swissdentalline.mapper.TerminMapper;
import ch.zhaw.swissdentalline.model.Termin;
import ch.zhaw.swissdentalline.model.TerminStatus;
import ch.zhaw.swissdentalline.repositories.TerminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TerminService {

    private final TerminRepository terminRepository;
    private final TerminMapper terminMapper;

    public Termin createTermin(TerminCreateDTO createDTO) {
        Termin termin = terminMapper.toEntity(createDTO);
        return terminRepository.save(termin);
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
