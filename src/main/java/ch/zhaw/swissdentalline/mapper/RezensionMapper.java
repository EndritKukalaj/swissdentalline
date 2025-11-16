package ch.zhaw.swissdentalline.mapper;

import ch.zhaw.swissdentalline.dto.RezensionCreateDTO;
import ch.zhaw.swissdentalline.model.Rezension;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RezensionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "approved", ignore = true)
    @Mapping(target = "aiKommentar", ignore = true)
    Rezension toEntity(RezensionCreateDTO dto);
}
