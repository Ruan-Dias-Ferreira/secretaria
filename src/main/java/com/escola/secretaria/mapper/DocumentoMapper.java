package com.escola.secretaria.mapper;



import com.escola.secretaria.domain.Documento;
import com.escola.secretaria.dto.request.DocumentoRequest;
import com.escola.secretaria.dto.response.DocumentoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DocumentoMapper {
    @Mapping(source = "aluno.id", target = "alunoId")
    DocumentoResponse toResponse(Documento documento);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "dataEmissao", ignore = true)
    Documento toEntity(DocumentoRequest request);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "dataEmissao", ignore = true)
    void updateEntity(DocumentoRequest request, @MappingTarget Documento documento);
}
