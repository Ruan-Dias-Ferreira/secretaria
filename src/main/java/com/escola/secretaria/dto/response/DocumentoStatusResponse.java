package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.enums.TipoDocumento;

import java.time.LocalDate;

public record DocumentoStatusResponse(
        TipoDocumento tipo,
        boolean entregue,
        LocalDate dataEmissao,
        Long documentoId
) {}
