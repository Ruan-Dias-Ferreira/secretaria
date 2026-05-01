package com.escola.secretaria.dto.response;

public record AlunoResponse(
        Long id,
        String nome,
        String cpf,
        String email,
        String nomeResponsavel,
        RematriculadoResponse rematriculado
) {
}
