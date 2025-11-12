package ch.zhaw.swissdentalline.repositories;

import ch.zhaw.swissdentalline.model.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PatientRepository extends MongoRepository<Patient, String> {
    List<Patient> findByAdresseId(String adresseId);
    List<Patient> findByNameContaining(String namePart);
}
