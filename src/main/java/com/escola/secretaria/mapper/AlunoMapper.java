package com.escola.secretaria.mapper;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.dto.request.AlunoRequest;
import com.escola.secretaria.dto.response.AlunoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AlunoMapper {

    AlunoResponse toResponse(Aluno aluno);

    @Mapping(target = "id", ignore = true)
    Aluno toEntity(AlunoRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(AlunoRequest request, @MappingTarget Aluno aluno);
}
