package com.escola.secretaria.mapper;

import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Usuario;
import com.escola.secretaria.dto.request.DisciplinaRequest;
import com.escola.secretaria.dto.response.DisciplinaResponse;
import com.escola.secretaria.repository.UsuarioRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring")
public abstract class DisciplinaMapper {

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Mapping(target = "turmaId", source = "turma.id")
    @Mapping(target = "professorId", source = "professor.id")
    @Mapping(target = "professorLogin", source = "professor.login")
    public abstract DisciplinaResponse toResponse(Disciplina disciplina);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "turma", ignore = true)
    @Mapping(target = "professor", source = "professorId", qualifiedByName = "idToUsuario")
    public abstract Disciplina toEntity(DisciplinaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "turma", ignore = true)
    @Mapping(target = "professor", source = "professorId", qualifiedByName = "idToUsuario")
    public abstract void updateEntity(DisciplinaRequest request, @MappingTarget Disciplina disciplina);

    @Named("idToUsuario")
    protected Usuario idToUsuario(Long professorId) {
        if (professorId == null) return null;
        return usuarioRepository.getReferenceById(professorId);
    }
}
