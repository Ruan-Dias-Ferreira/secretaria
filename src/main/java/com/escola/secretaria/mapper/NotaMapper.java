package com.escola.secretaria.mapper;

import com.escola.secretaria.domain.Nota;
import com.escola.secretaria.dto.request.NotaRequest;
import com.escola.secretaria.dto.response.NotaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotaMapper {
    @Mapping(source = "aluno.id", target = "alunoId")
    @Mapping(source = "disciplina.id", target = "disciplinaId")
    NotaResponse toResponse(Nota nota);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "disciplina", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    Nota toEntity(NotaRequest request);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "disciplina", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    void updateEntity(NotaRequest request, @MappingTarget Nota nota);
}
