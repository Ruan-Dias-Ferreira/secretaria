package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.enums.StatusMatricula;

public record MatriculaResponse(
        Long id,
        int anoLetivo,
        StatusMatricula status,
        Long alunoId,
        String alunoNome,
        String alunoCpf,
        Long turmaId,
        String turmaNome,
        String turno,
        String curso
) {}
