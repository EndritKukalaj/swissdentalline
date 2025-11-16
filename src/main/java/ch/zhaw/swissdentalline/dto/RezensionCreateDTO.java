package ch.zhaw.swissdentalline.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RezensionCreateDTO {
    @NotBlank
    private String zahnarztId;

    @NotBlank
    private String patientId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer bewertung;

    @NotBlank
    private String text;

    @NotNull
    private Instant datum;
}
