package com.escola.secretaria.service;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Matricula;
import com.escola.secretaria.domain.Turma;
import com.escola.secretaria.domain.enums.StatusMatricula;
import com.escola.secretaria.dto.request.MatriculaRequest;
import com.escola.secretaria.dto.request.MatriculaStatusRequest;
import com.escola.secretaria.dto.response.MatriculaResponse;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.exception.RegraDeNegocioException;
import com.escola.secretaria.mapper.MatriculaMapper;
import com.escola.secretaria.repository.AlunoRepository;
import com.escola.secretaria.repository.MatriculaRepository;
import com.escola.secretaria.repository.TurmaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @InjectMocks
    private MatriculaService matriculaService;

    @Mock private MatriculaRepository matriculaRepository;
    @Mock private MatriculaMapper matriculaMapper;
    @Mock private TurmaRepository turmaRepository;
    @Mock private AlunoRepository alunoRepository;

    private Aluno aluno;
    private Turma turma;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);

        turma = new Turma();
        turma.setId(1L);
    }

    // ---- save - matrícula duplicada ----

    @Test
    @DisplayName("save deve lançar CONFLICT quando aluno já possui matrícula ATIVA no mesmo ano letivo")
    void save_DeveLancarConflict_QuandoAlunoJaTemMatriculaAtivaNoMesmoAnoLetivo() {
        // Arrange
        MatriculaRequest request = new MatriculaRequest(2024, StatusMatricula.ATIVA, 1L, 1L);
        Matricula matriculaExistente = new Matricula();
        matriculaExistente.setStatus(StatusMatricula.ATIVA);
        matriculaExistente.setAnoLetivo(2024);
        when(matriculaMapper.toEntity(request)).thenReturn(new Matricula());
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(matriculaRepository.findByAluno(aluno)).thenReturn(List.of(matriculaExistente));

        // Act & Assert
        var ex = assertThrows(
                RegraDeNegocioException.class,
                () -> matriculaService.save(request)
        );
        assertTrue(ex.getMessage().contains("matrícula ativa"));
        verify(matriculaRepository, never()).save(any());
    }

    @Test
    @DisplayName("save deve persistir matrícula quando aluno não possui matrícula ATIVA no mesmo ano letivo")
    void save_DevePersistirMatricula_QuandoNaoExisteConflito() {
        // Arrange
        MatriculaRequest request = new MatriculaRequest(2024, StatusMatricula.ATIVA, 1L, 1L);
        Matricula novaMatricula = new Matricula();
        when(matriculaMapper.toEntity(request)).thenReturn(novaMatricula);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(matriculaRepository.findByAluno(aluno)).thenReturn(List.of());
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(novaMatricula);
        when(matriculaMapper.toResponse(novaMatricula)).thenReturn(
                new MatriculaResponse(2024, 1L, 1L, StatusMatricula.ATIVA, 1L));

        // Act
        MatriculaResponse resultado = matriculaService.save(request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(matriculaRepository).save(any(Matricula.class));
    }

    @Test
    @DisplayName("save deve persistir matrícula quando matrícula existente é de outro ano letivo")
    void save_DevePersistir_QuandoMatriculaExistenteEhDeOutroAnoLetivo() {
        // Arrange
        MatriculaRequest request = new MatriculaRequest(2024, StatusMatricula.ATIVA, 1L, 1L);
        Matricula matricula2023 = new Matricula();
        matricula2023.setStatus(StatusMatricula.ATIVA);
        matricula2023.setAnoLetivo(2023); // ano diferente — não deve bloquear
        Matricula novaMatricula = new Matricula();
        when(matriculaMapper.toEntity(request)).thenReturn(novaMatricula);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(matriculaRepository.findByAluno(aluno)).thenReturn(List.of(matricula2023));
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(novaMatricula);
        when(matriculaMapper.toResponse(novaMatricula)).thenReturn(
                new MatriculaResponse(2024, 1L, 1L, StatusMatricula.ATIVA, 1L));

        // Act
        MatriculaResponse resultado = matriculaService.save(request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(matriculaRepository).save(any(Matricula.class));
    }

    // ---- updateStatus ----

    @Test
    @DisplayName("updateStatus deve persistir e retornar a matrícula com o novo status")
    void updateStatus_DevePersistirERetornarMatriculaComNovoStatus() {
        // Arrange
        MatriculaStatusRequest request = new MatriculaStatusRequest(StatusMatricula.CONCLUIDA);
        Matricula matricula = new Matricula();
        matricula.setId(1L);
        matricula.setStatus(StatusMatricula.ATIVA);
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(matricula);
        when(matriculaMapper.toResponse(matricula)).thenReturn(
                new MatriculaResponse(2024, 1L, 1L, StatusMatricula.CONCLUIDA, 1L));

        // Act
        MatriculaResponse resultado = matriculaService.updateStatus(1L, request);

        // Assert
        ArgumentCaptor<Matricula> captor = ArgumentCaptor.forClass(Matricula.class);
        verify(matriculaRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusMatricula.CONCLUIDA);
        assertThat(resultado.status()).isEqualTo(StatusMatricula.CONCLUIDA);
    }

    @Test
    @DisplayName("updateStatus deve lançar exceção quando a matrícula não é encontrada")
    void updateStatus_DeveLancarExcecao_QuandoMatriculaNaoEncontrada() {
        // Arrange
        MatriculaStatusRequest request = new MatriculaStatusRequest(StatusMatricula.CONCLUIDA);
        when(matriculaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        var ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> matriculaService.updateStatus(99L, request));
        assertTrue(ex.getMessage().contains("Matrícula não encontrada"));
        verify(matriculaRepository, never()).save(any());
    }
}
