package com.escola.secretaria.dto.response;

public record RematriculadoResponse(
        Long alunoId,
        String nome,
        String cpf,
        Integer anoLetivo,
        String serie,
        String turma,
        String turno
) {
}
