package ch.zhaw.swissdentalline.repositories;

import ch.zhaw.swissdentalline.model.Adresse;
import ch.zhaw.swissdentalline.model.AdressTyp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdresseRepository extends MongoRepository<Adresse, String> {
	Page<Adresse> findByTyp(AdressTyp typ, Pageable pageable);
}
