package ch.zhaw.swissdentalline.mapper;

import ch.zhaw.swissdentalline.dto.ZahnarztCreateDTO;
import ch.zhaw.swissdentalline.model.Zahnarzt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ZahnarztMapper {

    @Mapping(target = "id", ignore = true)
    Zahnarzt toEntity(ZahnarztCreateDTO dto);
}
