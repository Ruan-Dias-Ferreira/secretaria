package com.escola.secretaria.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AlunoRequest(
        @NotBlank String nome,
        @NotBlank String cpf,
        String rg,
        String tituloEleitor,
        @NotNull LocalDate dataNascimento,
        String email,
        String telefone,
        @NotBlank String telefoneResponsavel,
        @NotNull @Valid EnderecoDto endereco,
        @NotNull @Valid CertidaoNascimentoDto certidaoNascimento,
        @Valid ResponsavelDto mae,
        @Valid ResponsavelDto pai,
        @Valid ResponsavelDto responsavelLegal
) {
    @AssertTrue(message = "Pelo menos um responsável (mãe, pai ou legal) deve ter nome preenchido.")
    public boolean isAoMenosUmResponsavelComNome() {
        return temNome(mae) || temNome(pai) || temNome(responsavelLegal);
    }

    private static boolean temNome(ResponsavelDto r) {
        return r != null && r.nome() != null && !r.nome().isBlank();
    }
}
