package com.escola.secretaria.mapper;

import com.escola.secretaria.domain.Evento;
import com.escola.secretaria.dto.request.EventoRequest;
import com.escola.secretaria.dto.response.EventoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EventoMapper {
    EventoResponse toResponse(Evento evento);

    @Mapping(target = "id", ignore = true)
    Evento toEntity(EventoRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(EventoRequest request, @MappingTarget Evento evento);
}
