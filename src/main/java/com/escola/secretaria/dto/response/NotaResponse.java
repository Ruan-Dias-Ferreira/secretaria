package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.enums.SituacaoNota;

public record NotaResponse(
        double Valor,
        Long alunoId,
        Long disciplinaId,
        int bimestre,
        SituacaoNota situacaoNota,
        Long id
) {
}
