package ch.zhaw.swissdentalline.repositories;

import ch.zhaw.swissdentalline.dto.EinnahmenProMonatDTO;
import ch.zhaw.swissdentalline.dto.TerminStatusAggregationDTO;
import ch.zhaw.swissdentalline.model.Termin;
import ch.zhaw.swissdentalline.model.TerminStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;

import java.time.Instant;
import java.util.List;

public interface TerminRepository extends MongoRepository<Termin, String> {
    // Basic finders for core booking flows
    Page<Termin> findByStatusAndDatumBetween(TerminStatus status, Instant start, Instant end, Pageable pageable);

    Page<Termin> findByStatus(TerminStatus status, Pageable pageable);

    // Aggregation: distribution of appointment states for a dentist
    @Aggregation({
	    "{ '$match': { 'zahnarzt_id': ?0 } }",
	    "{ '$group': { '_id': '$status', 'count': { '$count': {} }, 'terminIds': { '$push': '$_id' } } }"
    })
    List<TerminStatusAggregationDTO> getTerminStateAggregation(String zahnarztId);

    // Aggregation: monthly revenue and count for completed appointments of a dentist within a date range
    @Aggregation({
	    "{ '$match': { 'zahnarzt_id': ?0, 'status': 'ABGESCHLOSSEN', 'datum': { '$gte': ?1, '$lte': ?2 } } }",
	    "{ '$group': { '_id': { '$dateToString': { 'format': '%Y-%m', 'date': '$datum' } }, 'revenue': { '$sum': '$preis' }, 'count': { '$count': {} } } }",
	    "{ '$sort': { '_id': 1 } }"
    })
    List<EinnahmenProMonatDTO> getMonthlyRevenueByDentist(String zahnarztId, Instant start, Instant end);
}
