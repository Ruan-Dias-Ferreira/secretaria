package com.escola.secretaria.service;

import com.escola.secretaria.AbstractIntegrationTest;
import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Matricula;
import com.escola.secretaria.domain.Turma;
import com.escola.secretaria.domain.enums.StatusMatricula;
import com.escola.secretaria.dto.request.MatriculaRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatriculaRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private MatriculaService matriculaService;

    private Aluno criarAluno() {
        Aluno aluno = new Aluno();
        aluno.setNome("Aluno Integração");
        aluno.setCpf("000.000.000-00");
        return alunoRepository.save(aluno);
    }

    private Turma criarTurma() {
        Turma turma = new Turma();
        turma.setNome("Turma A");
        turma.setAnoLetivo(2024);
        turma.setTurno("Manhã");
        turma.setCurso("Informática");
        return turmaRepository.save(turma);
    }

    @Test
    @DisplayName("save deve persistir matrícula no banco e gerar ID automático")
    void save_DevePersistirMatriculaEGerarId() {
        // Arrange
        Aluno aluno = criarAluno();
        Turma turma = criarTurma();
        Matricula matricula = new Matricula();
        matricula.setAnoLetivo(2024);
        matricula.setStatus(StatusMatricula.ATIVA);
        matricula.setAluno(aluno);
        matricula.setTurma(turma);

        // Act
        Matricula salva = matriculaRepository.save(matricula);

        // Assert
        assertThat(salva.getId()).isNotNull().isPositive();
        assertThat(salva.getStatus()).isEqualTo(StatusMatricula.ATIVA);
        assertThat(salva.getAnoLetivo()).isEqualTo(2024);
        assertThat(salva.getAluno().getId()).isEqualTo(aluno.getId());
    }

    @Test
    @DisplayName("findByAluno deve retornar todas as matrículas do aluno informado")
    void findByAluno_DeveRetornarMatriculasDoAluno() {
        // Arrange
        Aluno aluno = criarAluno();
        Aluno outroAluno = new Aluno();
        outroAluno.setNome("Outro Aluno");
        outroAluno = alunoRepository.save(outroAluno);

        Turma turma = criarTurma();

        Matricula m1 = new Matricula();
        m1.setAnoLetivo(2023);
        m1.setStatus(StatusMatricula.CONCLUIDA);
        m1.setAluno(aluno);
        m1.setTurma(turma);
        matriculaRepository.save(m1);

        Matricula m2 = new Matricula();
        m2.setAnoLetivo(2024);
        m2.setStatus(StatusMatricula.ATIVA);
        m2.setAluno(aluno);
        m2.setTurma(turma);
        matriculaRepository.save(m2);

        Matricula mOutro = new Matricula();
        mOutro.setAnoLetivo(2024);
        mOutro.setStatus(StatusMatricula.ATIVA);
        mOutro.setAluno(outroAluno);
        mOutro.setTurma(turma);
        matriculaRepository.save(mOutro);

        // Act
        List<Matricula> resultado = matriculaRepository.findByAluno(aluno);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(m -> m.getAluno().getId().equals(aluno.getId()));
    }

    @Test
    @DisplayName("save via service deve lançar CONFLICT quando aluno já possui matrícula ATIVA no mesmo ano letivo")
    void save_DeveLancarConflict_QuandoJaExisteMatriculaAtivaNoBanco() {
        // Arrange
        Aluno aluno = criarAluno();
        Turma turma = criarTurma();

        // Salva primeira matrícula ATIVA direto no repositório (simulando estado existente no banco)
        Matricula matriculaExistente = new Matricula();
        matriculaExistente.setAnoLetivo(2024);
        matriculaExistente.setStatus(StatusMatricula.ATIVA);
        matriculaExistente.setAluno(aluno);
        matriculaExistente.setTurma(turma);
        matriculaRepository.save(matriculaExistente);

        // Tenta salvar segunda matrícula ATIVA para o mesmo aluno e mesmo ano via service
        MatriculaRequest request = new MatriculaRequest(2024, StatusMatricula.ATIVA, turma.getId(), aluno.getId());

        // Act & Assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> matriculaService.save(request)
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(matriculaRepository.findByAluno(aluno)).hasSize(1);
    }
}
