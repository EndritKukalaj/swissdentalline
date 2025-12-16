package ch.zhaw.swissdentalline.model;

import ch.zhaw.swissdentalline.dto.PatientCreateDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Document("patienten")
public class Patient {

    @Id
    private String id;

    @NonNull
    private String name;

    private Instant geburtsdatum;

    private String krankenkasse;

    @Field("adresse_id")
    private String adresseId;

    /**
     * Factory Method: Erstellt einen neuen Patient aus einem PatientCreateDTO
     */
    public static Patient fromDTO(PatientCreateDTO dto) {
        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setGeburtsdatum(dto.getGeburtsdatum());
        patient.setKrankenkasse(dto.getKrankenkasse());
        patient.setAdresseId(dto.getAdresseId());
        return patient;
    }
}
