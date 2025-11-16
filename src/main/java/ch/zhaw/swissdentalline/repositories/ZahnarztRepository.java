package ch.zhaw.swissdentalline.repositories;

import ch.zhaw.swissdentalline.model.Zahnarzt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ZahnarztRepository extends MongoRepository<Zahnarzt, String> {
    List<Zahnarzt> findByPraxisAdresseId(String praxisAdresseId);

    List<Zahnarzt> findByName(String name);

    Optional<Zahnarzt> findByNameAndPraxisAdresseId(String name, String praxisAdresseId);
}
