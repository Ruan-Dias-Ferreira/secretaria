package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.enums.SituacaoNota;

public record NotaResponse(
        double valor,
        Long alunoId,
        Long disciplinaId,
        int bimestre,
        SituacaoNota situacaoNota,
        Long id
) {
}
