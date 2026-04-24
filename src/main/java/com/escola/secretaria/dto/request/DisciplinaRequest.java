package com.escola.secretaria.dto.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DisciplinaRequest(
        @NotBlank
        String nome,
        @NotNull
        @Positive
        int cargaHoraria,
        @NotNull
        @Positive
        Long turmaId,
        Long professorId
) {
}
