package com.escola.secretaria.dto.response;

import java.time.LocalDate;

public record RematriculaJanelaResponse(
        boolean disponivel,
        LocalDate inicio,
        LocalDate fim,
        int anoLetivoOrigem,
        int anoLetivoDestino
) {
}
