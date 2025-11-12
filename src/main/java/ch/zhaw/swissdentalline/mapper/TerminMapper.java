package ch.zhaw.swissdentalline.mapper;

import ch.zhaw.swissdentalline.dto.TerminCreateDTO;
import ch.zhaw.swissdentalline.model.Termin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TerminMapper {

    @Mapping(target = "id", ignore = true)
    Termin toEntity(TerminCreateDTO dto);
}
