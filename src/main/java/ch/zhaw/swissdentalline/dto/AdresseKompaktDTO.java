package ch.zhaw.swissdentalline.dto;

import ch.zhaw.swissdentalline.model.AdressTyp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdresseKompaktDTO {
    private String id;
    private String ort;
    private String plz;
    private AdressTyp typ;
}
