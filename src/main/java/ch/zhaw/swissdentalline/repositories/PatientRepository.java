package ch.zhaw.swissdentalline.repositories;

import ch.zhaw.swissdentalline.model.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends MongoRepository<Patient, String> {
    List<Patient> findByName(String name);

    Optional<Patient> findByNameAndGeburtsdatumAndAdresseId(String name, Instant geburtsdatum, String adresseId);
}
