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
}
