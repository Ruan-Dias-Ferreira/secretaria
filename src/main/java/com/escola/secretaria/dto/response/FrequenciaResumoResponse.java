package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.enums.SituacaoFrequencia;

public record FrequenciaResumoResponse(
        Long disciplinaId,
        String nomeDisciplina,
        int cargaHoraria,
        int presencas,
        Double percentual,
        SituacaoFrequencia situacao
) {
}
