package ch.zhaw.swissdentalline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfilDTO {
    
    // Auth0 Benutzerdaten
    private String name;
    private String email;
    private String role;
    
    // Patient-spezifische Daten aus Collection
    private String adresse;
    private Instant geburtsdatum;
    private String krankenkasse;
    
}
