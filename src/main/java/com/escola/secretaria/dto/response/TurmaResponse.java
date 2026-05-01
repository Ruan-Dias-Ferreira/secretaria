package com.escola.secretaria.dto.response;

public record TurmaResponse(
        String nome,
        String turno,
        int anoLetivo,
        String curso,
        Long id,
        boolean rematricula,
        boolean operavel
) {
}
