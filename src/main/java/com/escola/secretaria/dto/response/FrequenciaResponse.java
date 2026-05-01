package com.escola.secretaria.dto.response;

import java.time.LocalDate;

import com.escola.secretaria.domain.MotivoFalta;

public record FrequenciaResponse(
        LocalDate data,
        Boolean presente,
        MotivoFalta motivo,
        Long alunoId,
        Long disciplinaId,
        Long id
) {
}
