package com.escola.secretaria.dto.request;


import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FrequenciaRequest(
        @NotNull
        LocalDate data,
        @NotNull
        Boolean presente,
        @NotNull
        @Positive
        Long alunoId,
        @NotNull
        @Positive
        Long disciplinaId
        ) {
}
