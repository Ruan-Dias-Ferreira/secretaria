package com.escola.secretaria.service;

import com.escola.secretaria.AbstractIntegrationTest;
import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Frequencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FrequenciaRepositoryIT extends AbstractIntegrationTest {

    private Aluno criarAluno(String nome) {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        return alunoRepository.save(aluno);
    }

    private Disciplina criarDisciplina(String nome) {
        Disciplina disciplina = new Disciplina();
        disciplina.setNome(nome);
        disciplina.setCargaHoraria(40);
        return disciplinaRepository.save(disciplina);
    }

    private Frequencia criarFrequencia(Aluno aluno, Disciplina disciplina, LocalDate data, boolean presente) {
        Frequencia f = new Frequencia();
        f.setAluno(aluno);
        f.setDisciplina(disciplina);
        f.setData(data);
        f.setPresente(presente);
        return frequenciaRepository.save(f);
    }

    @Test
    @DisplayName("save deve persistir frequência com data LocalDate e gerar ID")
    void save_DevePersistirFrequenciaComDataCorreta() {
        // Arrange
        Aluno aluno = criarAluno("Aluno Integração");
        Disciplina disciplina = criarDisciplina("Física");
        LocalDate data = LocalDate.of(2024, 3, 15);

        // Act
        Frequencia salva = criarFrequencia(aluno, disciplina, data, true);

        // Assert
        assertThat(salva.getId()).isNotNull().isPositive();
        assertThat(salva.getData()).isEqualTo(LocalDate.of(2024, 3, 15));
        assertThat(salva.getPresente()).isTrue();
        assertThat(salva.getAluno().getId()).isEqualTo(aluno.getId());
        assertThat(salva.getDisciplina().getId()).isEqualTo(disciplina.getId());
    }

    @Test
    @DisplayName("findByAlunoAndDisciplina deve retornar apenas frequências do aluno na disciplina informada")
    void findByAlunoAndDisciplina_DeveRetornarFrequenciasFiltradas() {
        // Arrange
        Aluno aluno = criarAluno("Aluno Principal");
        Aluno outroAluno = criarAluno("Outro Aluno");
        Disciplina fisica = criarDisciplina("Física");
        Disciplina quimica = criarDisciplina("Química");

        LocalDate base = LocalDate.of(2024, 3, 1);

        // aluno em física: 2 registros (presente e ausente)
        criarFrequencia(aluno, fisica, base, true);
        criarFrequencia(aluno, fisica, base.plusDays(1), false);

        // aluno em química: 1 registro
        criarFrequencia(aluno, quimica, base, true);

        // outro aluno em física: 1 registro — não deve aparecer
        criarFrequencia(outroAluno, fisica, base, true);

        // Act
        List<Frequencia> resultado = frequenciaRepository.findByAlunoAndDisciplina(aluno, fisica);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(f -> f.getAluno().getId().equals(aluno.getId()));
        assertThat(resultado).allMatch(f -> f.getDisciplina().getId().equals(fisica.getId()));
        assertThat(resultado).extracting(Frequencia::getPresente).containsExactlyInAnyOrder(true, false);
    }
}
