package com.escola.secretaria.dto.request;


import java.time.LocalDate;

import com.escola.secretaria.domain.MotivoFalta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FrequenciaRequest(
        @NotNull
        LocalDate data,
        @NotNull
        Boolean presente,
        MotivoFalta motivo,
        @NotNull
        @Positive
        Long alunoId,
        @Positive
        Long disciplinaId
        ) {
}
