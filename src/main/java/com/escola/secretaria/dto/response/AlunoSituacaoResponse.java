package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.enums.StatusMatricula;

import java.util.List;

public record AlunoSituacaoResponse(
        Long alunoId,
        String alunoNome,
        StatusMatricula statusAtual,
        Integer anoLetivoAtual,
        Long turmaIdAtual,
        String turmaNomeAtual,
        String cursoAtual,
        String turnoAtual,
        Double percentualFrequencia,
        int totalMatriculas,
        int matriculasAtivas,
        List<MatriculaResponse> historicoMatriculas
) {}
