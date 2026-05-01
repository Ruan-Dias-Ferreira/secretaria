package com.escola.secretaria.service;

import com.escola.secretaria.domain.Turma;
import com.escola.secretaria.exception.RegraDeNegocioException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Bloqueia operações sobre turmas fictícias (anoLetivo no futuro — criadas no
 * período de rematrícula). Notas, frequência, vínculo de professor/disciplina
 * só são permitidas após o início efetivo do ano letivo.
 */
@Component
public class TurmaValidator {

    public void assertOperavel(Turma turma) {
        if (turma == null) return;
        int anoAtual = LocalDate.now().getYear();
        if (turma.getAnoLetivo() > anoAtual) {
            throw new RegraDeNegocioException(
                    "Turma \"" + turma.getNome() + "\" pertence ao ano letivo " + turma.getAnoLetivo()
                            + " e ainda não iniciou. Operação só será liberada em " + turma.getAnoLetivo() + ".");
        }
        if (turma.getAnoLetivo() < anoAtual) {
            throw new RegraDeNegocioException(
                    "Ano letivo " + turma.getAnoLetivo() + " encerrado. "
                            + "Turma \"" + turma.getNome() + "\" é somente leitura.");
        }
    }
}
