package ch.zhaw.swissdentalline.model;

import ch.zhaw.swissdentalline.dto.RezensionCreateDTO;
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
@Document("rezensionen")
public class Rezension {

    @Id
    private String id;

    @NonNull
    @Field("zahnarzt_id")
    private String zahnarztId;

    @NonNull
    @Field("patient_id")
    private String patientId;

    @NonNull
    private Integer bewertung; // 1-5

    @NonNull
    private String text;

    @NonNull
    private Instant datum;

    private boolean approved;

    @Field("ai_kommentar")
    private String aiKommentar; // may be null

    /**
     * Factory Method: Erstellt eine neue Rezension aus einem RezensionCreateDTO
     */
    public static Rezension fromDTO(RezensionCreateDTO dto) {
        Rezension rezension = new Rezension();
        rezension.setZahnarztId(dto.getZahnarztId());
        rezension.setPatientId(dto.getPatientId());
        rezension.setBewertung(dto.getBewertung());
        rezension.setText(dto.getText());
        rezension.setDatum(dto.getDatum());
        rezension.setApproved(false); // initially not approved
        return rezension;
    }
}
