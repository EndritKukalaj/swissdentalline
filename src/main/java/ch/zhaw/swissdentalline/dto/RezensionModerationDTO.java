package ch.zhaw.swissdentalline.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RezensionModerationDTO {
    private boolean approved;
    private String aiKommentar;
}
