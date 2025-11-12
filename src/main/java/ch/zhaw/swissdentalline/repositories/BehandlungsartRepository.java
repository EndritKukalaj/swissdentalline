package ch.zhaw.swissdentalline.repositories;

import ch.zhaw.swissdentalline.model.Behandlungsart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BehandlungsartRepository extends MongoRepository<Behandlungsart, String> {
}
