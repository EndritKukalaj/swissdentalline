package ch.zhaw.swissdentalline.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Document("zahnaerzte")
public class Zahnarzt {

    @Id
    private String id;

    @NonNull
    private String name;

    @NonNull
    @Field("praxis_adresse_id")
    private String praxisAdresseId;
}
