package ch.zhaw.swissdentalline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZahnarztProfilDTO {
    
    // Auth0 Benutzerdaten
    private String name;
    private String email;
    private String role;
    
    // Zahnarzt-spezifische Daten aus Collection
    private String praxisname;
    private String praxisadresse;
}
