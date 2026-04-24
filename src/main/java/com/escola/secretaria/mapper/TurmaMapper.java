package com.escola.secretaria.mapper;

import com.escola.secretaria.domain.Turma;
import com.escola.secretaria.dto.request.TurmaRequest;
import com.escola.secretaria.dto.response.TurmaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TurmaMapper {
    TurmaResponse toResponse(Turma turma);
    @Mapping(target="id",ignore = true)
    Turma toEntity(TurmaRequest request);

    @Mapping(target="id",ignore = true)
    void updateEntity(TurmaRequest request, @MappingTarget Turma turma);
}
