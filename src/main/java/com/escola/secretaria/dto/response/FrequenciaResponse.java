package com.escola.secretaria.dto.response;

import java.time.LocalDate;

public record FrequenciaResponse(
        LocalDate data,
        Boolean presente,
        Long alunoId,
        Long disciplinaId,
        Long id
) {
}
