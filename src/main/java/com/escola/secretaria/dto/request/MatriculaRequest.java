package com.escola.secretaria.dto.request;

import com.escola.secretaria.domain.enums.StatusMatricula;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MatriculaRequest(
        @NotNull
        @Positive
        int anoLetivo,
        @NotNull
        StatusMatricula status,
        @NotNull
        @Positive
        Long turmaId,
        @NotNull
        @Positive
        Long alunoId
) {
}
