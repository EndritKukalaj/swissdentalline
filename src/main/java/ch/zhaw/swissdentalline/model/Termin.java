package ch.zhaw.swissdentalline.model;

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
@Document("termine")
public class Termin {

    @Id
    private String id;

    @NonNull
    @Field("zahnarzt_id")
    private String zahnarztId;

    @Field("patient_id")
    private String patientId; // nullable

    @NonNull
    @Field("behandlungsart_id")
    private String behandlungsartId;

    @NonNull
    private Instant datum;

    @NonNull
    @Field("dauer_minuten")
    private Integer dauerMinuten;

    @NonNull
    private Double preis;

    @NonNull
    private TerminStatus status;

    @Field("warteliste_aktiv")
    private boolean wartelisteAktiv;
}
