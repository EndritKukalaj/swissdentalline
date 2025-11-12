package ch.zhaw.swissdentalline.mapper;

import ch.zhaw.swissdentalline.dto.BehandlungsartCreateDTO;
import ch.zhaw.swissdentalline.model.Behandlungsart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BehandlungsartMapper {

    @Mapping(target = "id", ignore = true)
    Behandlungsart toEntity(BehandlungsartCreateDTO dto);
}
