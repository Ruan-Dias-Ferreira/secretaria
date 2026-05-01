package com.escola.secretaria.dto.response;

public record MatriculaStatsResponse(
        long total,
        long ativas,
        long transferidas,
        long canceladas
) {}
