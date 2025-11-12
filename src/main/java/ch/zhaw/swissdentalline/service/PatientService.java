package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.PatientCreateDTO;
import ch.zhaw.swissdentalline.mapper.PatientMapper;
import ch.zhaw.swissdentalline.model.Patient;
import ch.zhaw.swissdentalline.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public Patient createPatient(PatientCreateDTO createDTO) {
        Patient patient = patientMapper.toEntity(createDTO);
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
}
