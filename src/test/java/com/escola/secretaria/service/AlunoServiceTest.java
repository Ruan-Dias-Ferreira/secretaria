package com.escola.secretaria.service;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Frequencia;
import com.escola.secretaria.domain.enums.SituacaoFrequencia;
import com.escola.secretaria.dto.response.FrequenciaResumoResponse;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.mapper.AlunoMapper;
import com.escola.secretaria.repository.AlunoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @InjectMocks
    private AlunoService alunoService;

    @Mock private AlunoRepository alunoRepository;
    @Mock private AlunoMapper alunoMapper;
    @Mock private NotaService notaService;
    @Mock private FrequenciaService frequenciaService;

    private Aluno aluno;
    private Disciplina disciplina;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);

        disciplina = new Disciplina();
        disciplina.setId(1L);
        disciplina.setNome("Física");
        disciplina.setCargaHoraria(4);
    }

    // ---- getFrequenciasPorAluno - cálculo de percentual e classificação ----

    @Test
    @DisplayName("getFrequenciasPorAluno deve retornar REGULAR quando percentual de presença é exatamente 75%")
    void getFrequenciasPorAluno_DeveRetornarREGULAR_QuandoPercentualIgualA75() {
        // Arrange
        // 3 presenças em cargaHoraria=4 → (3/4.0)*100 = 75.0% → REGULAR
        List<Frequencia> frequencias = List.of(
                criarFrequencia(true),
                criarFrequencia(true),
                criarFrequencia(true),
                criarFrequencia(false)
        );
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(frequenciaService.findByAluno(aluno)).thenReturn(frequencias);

        // Act
        List<FrequenciaResumoResponse> resultado = alunoService.getFrequenciasPorAluno(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).situacao()).isEqualTo(SituacaoFrequencia.REGULAR);
        assertThat(resultado.get(0).percentual()).isEqualTo(75.0);
        assertThat(resultado.get(0).presencas()).isEqualTo(3);
        assertThat(resultado.get(0).cargaHoraria()).isEqualTo(4);
    }

    @Test
    @DisplayName("getFrequenciasPorAluno deve retornar REGULAR quando percentual de presença é maior que 75%")
    void getFrequenciasPorAluno_DeveRetornarREGULAR_QuandoPercentualMaiorQue75() {
        // Arrange
        // 4 presenças em cargaHoraria=4 → (4/4.0)*100 = 100.0% → REGULAR
        List<Frequencia> frequencias = List.of(
                criarFrequencia(true),
                criarFrequencia(true),
                criarFrequencia(true),
                criarFrequencia(true)
        );
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(frequenciaService.findByAluno(aluno)).thenReturn(frequencias);

        // Act
        List<FrequenciaResumoResponse> resultado = alunoService.getFrequenciasPorAluno(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).situacao()).isEqualTo(SituacaoFrequencia.REGULAR);
        assertThat(resultado.get(0).percentual()).isEqualTo(100.0);
        assertThat(resultado.get(0).presencas()).isEqualTo(4);
    }

    @Test
    @DisplayName("getFrequenciasPorAluno deve retornar REPROVADO_FREQUENCIA quando percentual de presença é menor que 75%")
    void getFrequenciasPorAluno_DeveRetornarREPROVADO_QuandoPercentualMenorQue75() {
        // Arrange
        // 2 presenças em cargaHoraria=4 → (2/4.0)*100 = 50.0% → REPROVADO_FREQUENCIA
        List<Frequencia> frequencias = List.of(
                criarFrequencia(true),
                criarFrequencia(true),
                criarFrequencia(false),
                criarFrequencia(false)
        );
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(frequenciaService.findByAluno(aluno)).thenReturn(frequencias);

        // Act
        List<FrequenciaResumoResponse> resultado = alunoService.getFrequenciasPorAluno(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).situacao()).isEqualTo(SituacaoFrequencia.REPROVADO_FREQUENCIA);
        assertThat(resultado.get(0).percentual()).isEqualTo(50.0);
        assertThat(resultado.get(0).presencas()).isEqualTo(2);
    }

    @Test
    @DisplayName("getFrequenciasPorAluno deve retornar REPROVADO_FREQUENCIA quando nenhuma presença foi registrada")
    void getFrequenciasPorAluno_DeveRetornarREPROVADO_QuandoNenhumaPresenca() {
        // Arrange
        // 0 presenças em cargaHoraria=4 → (0/4.0)*100 = 0.0% → REPROVADO_FREQUENCIA
        List<Frequencia> frequencias = List.of(
                criarFrequencia(false),
                criarFrequencia(false)
        );
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(frequenciaService.findByAluno(aluno)).thenReturn(frequencias);

        // Act
        List<FrequenciaResumoResponse> resultado = alunoService.getFrequenciasPorAluno(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).situacao()).isEqualTo(SituacaoFrequencia.REPROVADO_FREQUENCIA);
        assertThat(resultado.get(0).percentual()).isEqualTo(0.0);
        assertThat(resultado.get(0).presencas()).isEqualTo(0);
    }

    @Test
    @DisplayName("getFrequenciasPorAluno deve lançar exceção quando aluno não é encontrado")
    void getFrequenciasPorAluno_DeveLancarExcecao_QuandoAlunoNaoEncontrado() {
        // Arrange
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        var ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> alunoService.getFrequenciasPorAluno(99L));
        assertTrue(ex.getMessage().contains("Aluno não encontrado"));
    }

    private Frequencia criarFrequencia(boolean presente) {
        Frequencia f = new Frequencia();
        f.setPresente(presente);
        f.setDisciplina(disciplina);
        f.setAluno(aluno);
        f.setData(LocalDate.now());
        return f;
    }
}
