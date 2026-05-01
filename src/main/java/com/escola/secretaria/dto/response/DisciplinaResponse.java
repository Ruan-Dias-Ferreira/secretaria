package com.escola.secretaria.dto.response;

public record DisciplinaResponse(
        Long id,
        String nome,
        int cargaHoraria,
        Long turmaId,
        Integer turmaAnoLetivo,
        Boolean turmaOperavel,
        Long professorId,
        String professorLogin
) {
}
