package com.escola.secretaria.mapper;



import com.escola.secretaria.domain.Frequencia;
import com.escola.secretaria.dto.request.FrequenciaRequest;
import com.escola.secretaria.dto.response.FrequenciaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FrequenciaMapper {
    @Mapping(source = "aluno.id", target = "alunoId")
    @Mapping(source = "disciplina.id", target = "disciplinaId")
    FrequenciaResponse toResponse(Frequencia frequencia);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "disciplina", ignore = true)
    Frequencia toEntity(FrequenciaRequest request);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "disciplina", ignore = true)
    void updateEntity(FrequenciaRequest request,@MappingTarget Frequencia frequencia);
}
