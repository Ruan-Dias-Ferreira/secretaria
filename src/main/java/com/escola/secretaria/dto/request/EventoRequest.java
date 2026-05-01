package com.escola.secretaria.dto.request;

import com.escola.secretaria.domain.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EventoRequest(
        @NotNull LocalDate data,
        @NotNull TipoEvento tipo,
        @NotBlank String titulo,
        String descricao
) {
}
