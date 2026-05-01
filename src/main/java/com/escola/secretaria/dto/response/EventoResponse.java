package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.TipoEvento;

import java.time.LocalDate;

public record EventoResponse(
        Long id,
        LocalDate data,
        TipoEvento tipo,
        String titulo,
        String descricao
) {
}
