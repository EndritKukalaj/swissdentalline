package ch.zhaw.swissdentalline.model;

import ch.zhaw.swissdentalline.dto.BehandlungsartCreateDTO;
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

    /**
     * Factory Method: Erstellt eine neue Behandlungsart aus einem BehandlungsartCreateDTO
     */
    public static Behandlungsart fromDTO(BehandlungsartCreateDTO dto) {
        Behandlungsart behandlungsart = new Behandlungsart();
        behandlungsart.setName(dto.getName());
        behandlungsart.setBeschreibung(dto.getBeschreibung());
        return behandlungsart;
    }
}
