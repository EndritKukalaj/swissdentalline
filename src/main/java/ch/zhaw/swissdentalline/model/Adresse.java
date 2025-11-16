package ch.zhaw.swissdentalline.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Document("adressen")
public class Adresse {

    @Id
    private String id;

    @NonNull
    private String strasse;

    @NonNull
    private String plz;

    @NonNull
    private String ort;

    @NonNull
    private AdressTyp typ;

    private String bezeichnung;
}
