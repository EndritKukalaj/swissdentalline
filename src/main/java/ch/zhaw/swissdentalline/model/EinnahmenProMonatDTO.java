package ch.zhaw.swissdentalline.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EinnahmenProMonatDTO {

    @Field("_id")
    private String monat; // format: YYYY-MM

    private double einnahmen;

    private long anzahl;
}
