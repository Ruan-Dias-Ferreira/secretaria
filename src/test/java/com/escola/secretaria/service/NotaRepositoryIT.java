package com.escola.secretaria.service;

import com.escola.secretaria.AbstractIntegrationTest;
import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Nota;
import com.escola.secretaria.domain.enums.SituacaoNota;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NotaRepositoryIT extends AbstractIntegrationTest {

    private Aluno criarAluno(String nome) {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        return alunoRepository.save(aluno);
    }

    private Disciplina criarDisciplina(String nome, int cargaHoraria) {
        Disciplina disciplina = new Disciplina();
        disciplina.setNome(nome);
        disciplina.setCargaHoraria(cargaHoraria);
        return disciplinaRepository.save(disciplina);
    }

    private Nota criarNota(Aluno aluno, Disciplina disciplina, double valor, int bimestre) {
        Nota nota = new Nota();
        nota.setAluno(aluno);
        nota.setDisciplina(disciplina);
        nota.setValor(valor);
        nota.setBimestre(bimestre);
        nota.setSituacao(valor >= 6.0 ? SituacaoNota.APROVADO : SituacaoNota.RECUPERACAO);
        return nota;
    }

    @Test
    @DisplayName("save deve persistir nota com aluno e disciplina válidos e gerar ID")
    void save_DevePersistirNotaComAlunoEDisciplinaValidos() {
        // Arrange
        Aluno aluno = criarAluno("Aluno Integração");
        Disciplina disciplina = criarDisciplina("Matemática", 80);
        Nota nota = criarNota(aluno, disciplina, 8.5, 1);

        // Act
        Nota salva = notaRepository.save(nota);

        // Assert
        assertThat(salva.getId()).isNotNull().isPositive();
        assertThat(salva.getValor()).isEqualTo(8.5);
        assertThat(salva.getBimestre()).isEqualTo(1);
        assertThat(salva.getSituacao()).isEqualTo(SituacaoNota.APROVADO);
        assertThat(salva.getAluno().getId()).isEqualTo(aluno.getId());
        assertThat(salva.getDisciplina().getId()).isEqualTo(disciplina.getId());
    }

    @Test
    @DisplayName("findByAluno deve retornar todas as notas do aluno informado e nenhuma de outro aluno")
    void findByAluno_DeveRetornarApenasNotasDoAluno() {
        // Arrange
        Aluno aluno = criarAluno("Aluno Principal");
        Aluno outroAluno = criarAluno("Outro Aluno");
        Disciplina matematica = criarDisciplina("Matemática", 80);
        Disciplina portugues = criarDisciplina("Português", 60);

        notaRepository.save(criarNota(aluno, matematica, 9.0, 1));
        notaRepository.save(criarNota(aluno, portugues, 5.5, 1));
        notaRepository.save(criarNota(outroAluno, matematica, 7.0, 1));

        // Act
        List<Nota> resultado = notaRepository.findByAluno(aluno);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(n -> n.getAluno().getId().equals(aluno.getId()));
        assertThat(resultado)
                .extracting(n -> n.getDisciplina().getNome())
                .containsExactlyInAnyOrder("Matemática", "Português");
    }
}
