package com.escola.secretaria.mapper;

import com.escola.secretaria.domain.Turma;
import com.escola.secretaria.dto.request.TurmaRequest;
import com.escola.secretaria.dto.response.TurmaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TurmaMapper {
    @Mapping(target = "operavel",
            expression = "java(turma.getAnoLetivo() == java.time.LocalDate.now().getYear())")
    TurmaResponse toResponse(Turma turma);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rematricula", ignore = true)
    Turma toEntity(TurmaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rematricula", ignore = true)
    void updateEntity(TurmaRequest request, @MappingTarget Turma turma);
}
