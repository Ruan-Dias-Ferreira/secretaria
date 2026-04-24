package com.escola.secretaria.dto.response;

import java.time.LocalDate;

public record AlunoDetalheResponse(
                String nome,
                String cpf,
                String rg,
                LocalDate dataNascimento,
                String email,
                String telefone,
                String endereco,
                String nomeMae,
                String nomePai,
                Long id) {
}
