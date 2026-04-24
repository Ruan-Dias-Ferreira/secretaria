package com.escola.secretaria.dto.response;

public record DisciplinaResponse(
        Long id,
        String nome,
        int cargaHoraria,
        Long turmaId,
        Long professorId,
        String professorLogin
) {
}
