package com.escola.secretaria.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TurmaRequest(
        @NotBlank
        String nome,
        @NotNull
        @Positive
        int anoLetivo,
        @NotBlank
        String turno,
        @NotBlank
        String curso
) {
}
