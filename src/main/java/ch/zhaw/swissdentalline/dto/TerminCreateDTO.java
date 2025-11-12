package ch.zhaw.swissdentalline.dto;

import ch.zhaw.swissdentalline.model.TerminStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TerminCreateDTO {
    @NotBlank
    private String zahnarztId;

    private String patientId; // optional

    @NotBlank
    private String behandlungsartId;

    @NotNull
    private Instant datum;

    @NotNull
    @Positive
    private Integer dauerMinuten;

    @NotNull
    @PositiveOrZero
    private Double preis;

    @NotNull
    private TerminStatus status;

    private boolean wartelisteAktiv;
}
