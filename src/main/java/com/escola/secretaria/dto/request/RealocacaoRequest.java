package com.escola.secretaria.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RealocacaoRequest(
        @NotNull @Positive Long alunoId,
        @NotNull @Positive Long novaTurmaId
) {
}
