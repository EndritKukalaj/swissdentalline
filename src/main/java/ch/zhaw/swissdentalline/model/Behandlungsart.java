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
@Document("behandlungsarten")
public class Behandlungsart {

    @Id
    private String id;

    @NonNull
    private String name;

    @NonNull
    private String beschreibung;
}
