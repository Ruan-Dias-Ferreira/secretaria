package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.enums.StatusMatricula;

public record MatriculaResponse(
        int anoLetivo,
        Long turmaId,
        Long alunoId,
        StatusMatricula status,
        Long id

) {
}
