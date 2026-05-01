package com.escola.secretaria.mapper;

import com.escola.secretaria.domain.Matricula;
import com.escola.secretaria.dto.request.MatriculaRequest;
import com.escola.secretaria.dto.response.MatriculaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MatriculaMapper {
    @Mapping(source = "aluno.id",      target = "alunoId")
    @Mapping(source = "aluno.nome",    target = "alunoNome")
    @Mapping(source = "aluno.cpf",     target = "alunoCpf")
    @Mapping(source = "turma.id",      target = "turmaId")
    @Mapping(source = "turma.nome",    target = "turmaNome")
    @Mapping(source = "turma.turno",   target = "turno")
    @Mapping(source = "turma.curso",   target = "curso")
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
