package ch.zhaw.swissdentalline.dto;

import ch.zhaw.swissdentalline.model.AdressTyp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdresseCreateDTO {
    @NotBlank
    private String strasse;

    @NotBlank
    private String plz;

    @NotBlank
    private String ort;

    @NotNull
    private AdressTyp typ;

    private String bezeichnung;
}
