package ch.zhaw.swissdentalline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientCreateDTO {
    @NotBlank
    private String name;

    @NotNull
    private Instant geburtsdatum;

    private String krankenkasse;

    @NotBlank
    private String adresseId;
}
