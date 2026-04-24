package com.escola.secretaria.dto.request;

import com.escola.secretaria.domain.enums.TipoDocumento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DocumentoRequest(
                @NotNull
                TipoDocumento tipo,
                @NotNull
                @Positive
                Long alunoId) {
}
