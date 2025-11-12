package ch.zhaw.swissdentalline.repositories;

import ch.zhaw.swissdentalline.dto.GesamtBewertungDTO;
import ch.zhaw.swissdentalline.model.Rezension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RezensionRepository extends MongoRepository<Rezension, String> {

    // Average rating for a dentist (approved only)
    @Aggregation({
            "{ '$match': { 'zahnarzt_id': ?0, 'approved': true } }",
            "{ '$group': { '_id': null, 'avgBewertung': { '$avg': '$bewertung' }, 'count': { '$count': {} } } }"
    })
    List<GesamtBewertungDTO> getGesamtBewertungById(String id);

    Page<Rezension> findByZahnarztId(String zahnarztId, Pageable pageable);
}
