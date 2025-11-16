package ch.zhaw.swissdentalline.repositories;

import ch.zhaw.swissdentalline.model.Behandlungsart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BehandlungsartRepository extends MongoRepository<Behandlungsart, String> {
    Optional<Behandlungsart> findByName(String name);
}
