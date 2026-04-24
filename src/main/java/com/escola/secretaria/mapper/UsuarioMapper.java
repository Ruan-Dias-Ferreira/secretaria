package com.escola.secretaria.mapper;

import com.escola.secretaria.domain.Usuario;
import com.escola.secretaria.dto.request.UsuarioRequest;
import com.escola.secretaria.dto.response.UsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioResponse toResponse(Usuario usuario);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    Usuario toEntity(UsuarioRequest request);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    void updateEntity(UsuarioRequest request, @MappingTarget Usuario usuario);
}
