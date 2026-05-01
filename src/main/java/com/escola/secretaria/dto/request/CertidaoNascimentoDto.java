package com.escola.secretaria.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;

public record CertidaoNascimentoDto(
        @Pattern(regexp = "^$|^\\d{32}$",
                message = "Matrícula deve conter exatamente 32 dígitos.")
        String matricula,
        String livro,
        String folha,
        String termo
) {
    @AssertTrue(message = "Informe a matrícula (32 dígitos) OU livro, folha e termo.")
    public boolean isFormatoValido() {
        boolean novo = matricula != null && !matricula.isBlank();
        boolean antigo = !blank(livro) && !blank(folha) && !blank(termo);
        return novo || antigo;
    }

    private static boolean blank(String s) { return s == null || s.isBlank(); }
}
