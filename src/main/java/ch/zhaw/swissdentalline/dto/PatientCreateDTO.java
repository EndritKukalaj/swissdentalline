package ch.zhaw.swissdentalline.dto;

import jakarta.validation.constraints.NotBlank;
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
    
    private String id;
    
    @NotBlank
    private String name;

    private Instant geburtsdatum;

    private String krankenkasse;

    private String adresseId;
}
