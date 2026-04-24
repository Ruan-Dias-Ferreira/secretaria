package com.escola.secretaria.service;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Matricula;
import com.escola.secretaria.domain.Nota;
import com.escola.secretaria.domain.Usuario;
import com.escola.secretaria.domain.enums.Role;
import com.escola.secretaria.domain.enums.SituacaoNota;
import com.escola.secretaria.domain.enums.StatusMatricula;
import com.escola.secretaria.dto.request.NotaRequest;
import com.escola.secretaria.dto.response.BoletimResponse;
import com.escola.secretaria.dto.response.NotaResponse;
import com.escola.secretaria.exception.AcessoNegadoDisciplinaException;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.mapper.NotaMapper;
import com.escola.secretaria.repository.AlunoRepository;
import com.escola.secretaria.repository.DisciplinaRepository;
import com.escola.secretaria.repository.NotaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

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
class NotaServiceTest {

    @InjectMocks
    private NotaService notaService;

    @Mock private NotaRepository notaRepository;
    @Mock private NotaMapper notaMapper;
    @Mock private AlunoRepository alunoRepository;
    @Mock private DisciplinaRepository disciplinaRepository;
    @Mock private MatriculaService matriculaService;

    private Aluno aluno;
    private Disciplina disciplina;
    private Usuario professorLogado;
    private Usuario secretariaLogada;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);

        professorLogado = new Usuario();
        professorLogado.setId(10L);
        professorLogado.setRole(Role.PROFESSOR);

        secretariaLogada = new Usuario();
        secretariaLogada.setId(20L);
        secretariaLogada.setRole(Role.SECRETARIA);

        disciplina = new Disciplina();
        disciplina.setId(1L);
        disciplina.setNome("Matemática");
        disciplina.setCargaHoraria(80);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void configurarContextoSeguranca(Usuario usuario) {
        var context = new SecurityContextImpl();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities())
        );
        SecurityContextHolder.setContext(context);
    }

    // ---- calcularSituacaoBimestre (testado via save) ----

    @Test
    @DisplayName("save deve atribuir situação APROVADO quando valor da nota é maior ou igual a 6.0")
    void save_DeveAtribuirSituacaoAprovado_QuandoValorMaiorOuIgualASeis() {
        // Arrange
        configurarContextoSeguranca(secretariaLogada);
        disciplina.setProfessor(null);
        NotaRequest request = new NotaRequest(1, 7.0, 1L, 1L);
        Nota nota = new Nota();
        when(notaMapper.toEntity(request)).thenReturn(nota);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(disciplinaRepository.findById(1L)).thenReturn(Optional.of(disciplina));
        when(notaRepository.save(any(Nota.class))).thenReturn(nota);
        when(notaMapper.toResponse(nota)).thenReturn(
                new NotaResponse(7.0, 1L, 1L, 1, SituacaoNota.APROVADO, 1L));

        // Act
        notaService.save(request);

        // Assert
        ArgumentCaptor<Nota> captor = ArgumentCaptor.forClass(Nota.class);
        verify(notaRepository).save(captor.capture());
        assertThat(captor.getValue().getSituacao()).isEqualTo(SituacaoNota.APROVADO);
    }

    @Test
    @DisplayName("save deve atribuir situação RECUPERACAO quando valor da nota é menor que 6.0")
    void save_DeveAtribuirSituacaoRecuperacao_QuandoValorMenorQueSeis() {
        // Arrange
        configurarContextoSeguranca(secretariaLogada);
        disciplina.setProfessor(null);
        NotaRequest request = new NotaRequest(1, 5.9, 1L, 1L);
        Nota nota = new Nota();
        when(notaMapper.toEntity(request)).thenReturn(nota);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(disciplinaRepository.findById(1L)).thenReturn(Optional.of(disciplina));
        when(notaRepository.save(any(Nota.class))).thenReturn(nota);
        when(notaMapper.toResponse(nota)).thenReturn(
                new NotaResponse(5.9, 1L, 1L, 1, SituacaoNota.RECUPERACAO, 1L));

        // Act
        notaService.save(request);

        // Assert
        ArgumentCaptor<Nota> captor = ArgumentCaptor.forClass(Nota.class);
        verify(notaRepository).save(captor.capture());
        assertThat(captor.getValue().getSituacao()).isEqualTo(SituacaoNota.RECUPERACAO);
    }

    // ---- save - recurso não encontrado ----

    @Test
    @DisplayName("save deve lançar exceção quando o aluno não é encontrado")
    void save_DeveLancarExcecao_QuandoAlunoNaoEncontrado() {
        // Arrange
        configurarContextoSeguranca(secretariaLogada);
        NotaRequest request = new NotaRequest(1, 7.0, 99L, 1L);
        when(notaMapper.toEntity(request)).thenReturn(new Nota());
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        var ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> notaService.save(request));
        assertTrue(ex.getMessage().contains("Aluno não encontrado"));
        verify(notaRepository, never()).save(any());
    }

    @Test
    @DisplayName("save deve lançar exceção quando a disciplina não é encontrada")
    void save_DeveLancarExcecao_QuandoDisciplinaNaoEncontrada() {
        // Arrange
        configurarContextoSeguranca(secretariaLogada);
        NotaRequest request = new NotaRequest(1, 7.0, 1L, 99L);
        when(notaMapper.toEntity(request)).thenReturn(new Nota());
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(disciplinaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        var ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> notaService.save(request));
        assertTrue(ex.getMessage().contains("Disciplina não encontrada"));
        verify(notaRepository, never()).save(any());
    }

    // ---- save - controle de acesso por professor ----

    @Test
    @DisplayName("save deve lançar FORBIDDEN quando professor tenta salvar nota de disciplina que não é sua")
    void save_DeveLancarForbidden_QuandoProfessorAcessaDisciplinaAlheia() {
        // Arrange
        configurarContextoSeguranca(professorLogado);
        Usuario outroProfessor = new Usuario();
        outroProfessor.setId(99L);
        outroProfessor.setRole(Role.PROFESSOR);
        disciplina.setProfessor(outroProfessor);
        NotaRequest request = new NotaRequest(1, 7.0, 1L, 1L);
        when(notaMapper.toEntity(request)).thenReturn(new Nota());
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(disciplinaRepository.findById(1L)).thenReturn(Optional.of(disciplina));

        // Act & Assert
        var ex = assertThrows(
                AcessoNegadoDisciplinaException.class,
                () -> notaService.save(request)
        );
        assertTrue(ex.getMessage().contains("permissão"));
        verify(notaRepository, never()).save(any());
    }

    @Test
    @DisplayName("save deve persistir a nota quando o professor é o responsável pela disciplina")
    void save_DevePersistirNota_QuandoProfessorEhResponsavelPelaDisciplina() {
        // Arrange
        configurarContextoSeguranca(professorLogado);
        disciplina.setProfessor(professorLogado);
        NotaRequest request = new NotaRequest(1, 8.0, 1L, 1L);
        Nota nota = new Nota();
        when(notaMapper.toEntity(request)).thenReturn(nota);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(disciplinaRepository.findById(1L)).thenReturn(Optional.of(disciplina));
        when(notaRepository.save(any(Nota.class))).thenReturn(nota);
        when(notaMapper.toResponse(nota)).thenReturn(
                new NotaResponse(8.0, 1L, 1L, 1, SituacaoNota.APROVADO, 1L));

        // Act
        notaService.save(request);

        // Assert
        verify(notaRepository).save(any(Nota.class));
    }

    // ---- getBoletim - status especial DESISTENTE / TRANSFERIDO ----

    @Test
    @DisplayName("getBoletim deve retornar situação DESISTENTE com média 0.0 quando status da matrícula é DESISTENTE")
    void getBoletim_DeveRetornarDesistente_QuandoStatusMatriculaEhDesistente() {
        // Arrange
        Matricula matricula = new Matricula();
        matricula.setStatus(StatusMatricula.DESISTENTE);
        Nota nota = new Nota();
        nota.setValor(8.0);
        nota.setDisciplina(disciplina);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(matriculaService.findMaisRecentePorAluno(1L)).thenReturn(Optional.of(matricula));
        when(notaRepository.findByAluno(aluno)).thenReturn(List.of(nota));
        when(notaMapper.toResponse(nota)).thenReturn(
                new NotaResponse(8.0, 1L, 1L, 1, SituacaoNota.DESISTENTE, 1L));

        // Act
        List<BoletimResponse> resultado = notaService.getBoletim(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).situacaoNota()).isEqualTo(SituacaoNota.DESISTENTE);
        assertThat(resultado.get(0).mediaNotaDisciplina()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("getBoletim deve retornar situação TRANSFERIDO com média 0.0 quando status da matrícula é TRANSFERIDO")
    void getBoletim_DeveRetornarTransferido_QuandoStatusMatriculaEhTransferido() {
        // Arrange
        Matricula matricula = new Matricula();
        matricula.setStatus(StatusMatricula.TRANSFERIDO);
        Nota nota = new Nota();
        nota.setValor(6.0);
        nota.setDisciplina(disciplina);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(matriculaService.findMaisRecentePorAluno(1L)).thenReturn(Optional.of(matricula));
        when(notaRepository.findByAluno(aluno)).thenReturn(List.of(nota));
        when(notaMapper.toResponse(nota)).thenReturn(
                new NotaResponse(6.0, 1L, 1L, 1, SituacaoNota.TRANSFERIDO, 1L));

        // Act
        List<BoletimResponse> resultado = notaService.getBoletim(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).situacaoNota()).isEqualTo(SituacaoNota.TRANSFERIDO);
        assertThat(resultado.get(0).mediaNotaDisciplina()).isEqualTo(0.0);
    }

    // ---- getBoletim - calcularSituacaoFinal ----

    @Test
    @DisplayName("getBoletim deve calcular média e retornar APROVADO quando média das notas é maior ou igual a 6.0")
    void getBoletim_DeveRetornarAprovado_QuandoMediaMaiorOuIgualASeis() {
        // Arrange
        Nota nota1 = new Nota();
        nota1.setValor(8.0);
        nota1.setDisciplina(disciplina);
        Nota nota2 = new Nota();
        nota2.setValor(10.0);
        nota2.setDisciplina(disciplina);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(matriculaService.findMaisRecentePorAluno(1L)).thenReturn(Optional.empty());
        when(notaRepository.findByAluno(aluno)).thenReturn(List.of(nota1, nota2));
        when(notaMapper.toResponse(any(Nota.class))).thenReturn(
                new NotaResponse(9.0, 1L, 1L, 1, SituacaoNota.APROVADO, 1L));

        // Act
        List<BoletimResponse> resultado = notaService.getBoletim(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).situacaoNota()).isEqualTo(SituacaoNota.APROVADO);
        assertThat(resultado.get(0).mediaNotaDisciplina()).isEqualTo(9.0);
    }

    @Test
    @DisplayName("getBoletim deve calcular média e retornar REPROVADO quando média das notas é menor que 6.0")
    void getBoletim_DeveRetornarReprovado_QuandoMediaMenorQueSeis() {
        // Arrange
        Nota nota1 = new Nota();
        nota1.setValor(4.0);
        nota1.setDisciplina(disciplina);
        Nota nota2 = new Nota();
        nota2.setValor(5.0);
        nota2.setDisciplina(disciplina);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(matriculaService.findMaisRecentePorAluno(1L)).thenReturn(Optional.empty());
        when(notaRepository.findByAluno(aluno)).thenReturn(List.of(nota1, nota2));
        when(notaMapper.toResponse(any(Nota.class))).thenReturn(
                new NotaResponse(4.5, 1L, 1L, 1, SituacaoNota.REPROVADO, 1L));

        // Act
        List<BoletimResponse> resultado = notaService.getBoletim(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).situacaoNota()).isEqualTo(SituacaoNota.REPROVADO);
        assertThat(resultado.get(0).mediaNotaDisciplina()).isEqualTo(4.5);
    }
}
