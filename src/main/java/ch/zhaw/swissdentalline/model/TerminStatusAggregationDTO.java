package ch.zhaw.swissdentalline.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TerminStatusAggregationDTO {

    @Field("_id")
    private TerminStatus status;

    private long anzahl;

    private List<String> terminIds;
}
