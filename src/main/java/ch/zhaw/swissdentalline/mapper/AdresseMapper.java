package ch.zhaw.swissdentalline.mapper;

import ch.zhaw.swissdentalline.dto.AdresseCreateDTO;
import ch.zhaw.swissdentalline.dto.AdresseKompaktDTO;
import ch.zhaw.swissdentalline.model.Adresse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdresseMapper {

    @Mapping(target = "id", ignore = true)
    Adresse toEntity(AdresseCreateDTO dto);

    AdresseKompaktDTO toKompaktDTO(Adresse entity);
}
