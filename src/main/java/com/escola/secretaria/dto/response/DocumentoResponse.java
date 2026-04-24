package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.enums.TipoDocumento;

import java.time.LocalDate;

public record DocumentoResponse(
        TipoDocumento tipo,
        Long alunoId,
        LocalDate dataEmissao,
        Long id
) {
}
