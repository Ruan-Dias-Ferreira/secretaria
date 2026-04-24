package com.escola.secretaria.mapper;

import com.escola.secretaria.domain.Matricula;
import com.escola.secretaria.dto.request.MatriculaRequest;
import com.escola.secretaria.dto.response.MatriculaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MatriculaMapper {
    @Mapping(source = "turma.id", target = "turmaId")
    @Mapping(source = "aluno.id", target = "alunoId")
    MatriculaResponse toResponse(Matricula matricula);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "turma", ignore = true)
    Matricula toEntity(MatriculaRequest request);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "turma", ignore = true)
    void updateEntity(MatriculaRequest request, @MappingTarget Matricula matricula);
}
