package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.PatientCreateDTO;
import ch.zhaw.swissdentalline.dto.PatientProfilDTO;
import ch.zhaw.swissdentalline.mapper.PatientMapper;
import ch.zhaw.swissdentalline.model.Adresse;
import ch.zhaw.swissdentalline.model.Patient;
import ch.zhaw.swissdentalline.repositories.AdresseRepository;
import ch.zhaw.swissdentalline.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private AdresseRepository adresseRepository;

    public Patient createPatient(PatientCreateDTO createDTO) {
        // Validate foreign key: AdresseId must exist (only if provided)
        if (createDTO.getAdresseId() != null && !adresseRepository.existsById(createDTO.getAdresseId())) {
            throw new IllegalArgumentException("Adresse mit id: " + createDTO.getAdresseId() + " nicht gefunden");
        }

        // Check for duplicate patient (same name, geburtsdatum, and adresse)
        // Only check if adresseId is provided
        if (createDTO.getAdresseId() != null && 
            patientRepository.findByNameAndGeburtsdatumAndAdresseId(
                createDTO.getName(), createDTO.getGeburtsdatum(), createDTO.getAdresseId()).isPresent()) {
                throw new IllegalArgumentException("Patient mit gleichem Namen, Geburtsdatum und Adresse existiert bereits");
        }

        Patient patient = patientMapper.toEntity(createDTO);
        
        // If ID is provided in DTO (e.g., from Auth0 during signup), use it
        if (createDTO.getId() != null && !createDTO.getId().isEmpty()) {
            patient.setId(createDTO.getId());
        }
        
        return patientRepository.save(patient);
    }

    public Optional<Patient> getPatientById(String id) {
        return patientRepository.findById(id);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<Patient> findByName(String name) {
        return patientRepository.findByName(name);
    }

    public Patient updatePatient(String id, PatientCreateDTO updateDTO) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient mit id: " + id + " nicht gefunden"));

        // Validate foreign key
        if (!adresseRepository.existsById(updateDTO.getAdresseId())) {
            throw new IllegalArgumentException("Adresse mit id: " + updateDTO.getAdresseId() + " nicht gefunden");
        }

        // Check for duplicate if key fields changed
        Optional<Patient> duplicate = patientRepository.findByNameAndGeburtsdatumAndAdresseId(
                updateDTO.getName(), updateDTO.getGeburtsdatum(), updateDTO.getAdresseId());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException("Patient mit gleichem Namen, Geburtsdatum und Adresse existiert bereits");
        }

        existing.setName(updateDTO.getName());
        existing.setGeburtsdatum(updateDTO.getGeburtsdatum());
        existing.setKrankenkasse(updateDTO.getKrankenkasse());
        existing.setAdresseId(updateDTO.getAdresseId());

        return patientRepository.save(existing);
    }

    public void deletePatient(String id) {
        if (!patientRepository.existsById(id)) {
            throw new IllegalArgumentException("Patient mit id: " + id + " nicht gefunden");
        }
        patientRepository.deleteById(id);
    }

    public PatientProfilDTO getProfilByName(String name, String email, String role) {
        List<Patient> patients = patientRepository.findByName(name);
        
        if (patients.isEmpty()) {
            // Patient existiert noch nicht in Collection - gebe minimales Profil zurück
            PatientProfilDTO dto = new PatientProfilDTO();
            dto.setName(name);
            dto.setEmail(email);
            dto.setRole(role);
            return dto;
        }
        Patient patient = patients.get(0);
        
        // Lade Adresse
        String adresseFormatiert = null;
        if (patient.getAdresseId() != null) {
            Optional<Adresse> adresse = adresseRepository.findById(patient.getAdresseId());
            if (adresse.isPresent()) {
                Adresse adr = adresse.get();
                adresseFormatiert = String.format("%s, %s %s", adr.getStrasse(), adr.getPlz(), adr.getOrt());
            }
        }
        
        PatientProfilDTO dto = new PatientProfilDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setRole(role);
        dto.setAdresse(adresseFormatiert);
        dto.setGeburtsdatum(patient.getGeburtsdatum());
        dto.setKrankenkasse(patient.getKrankenkasse());
        
        return dto;
    }
}
