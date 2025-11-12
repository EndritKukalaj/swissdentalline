package ch.zhaw.swissdentalline.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BewertungDTO {
    private Double bewertung;
    private Long anzahl;
}
