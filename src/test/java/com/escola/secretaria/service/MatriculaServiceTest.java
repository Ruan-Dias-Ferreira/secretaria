package com.escola.secretaria.service;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Matricula;
import com.escola.secretaria.domain.RematriculaInfo;
import com.escola.secretaria.domain.Turma;
import com.escola.secretaria.domain.enums.StatusMatricula;
import com.escola.secretaria.dto.request.MatriculaRequest;
import com.escola.secretaria.dto.request.RematriculaRequest;
import com.escola.secretaria.dto.response.MatriculaResponse;
import com.escola.secretaria.exception.RegraDeNegocioException;
import com.escola.secretaria.mapper.MatriculaMapper;
import com.escola.secretaria.repository.AlunoRepository;
import com.escola.secretaria.repository.MatriculaRepository;
import com.escola.secretaria.repository.TurmaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    private MatriculaRepository matriculaRepository;
    @Mock
    private MatriculaMapper matriculaMapper;
    @Mock
    private TurmaRepository turmaRepository;
    @Mock
    private AlunoRepository alunoRepository;

    private MatriculaService service;

    private final int anoCorrente = LocalDate.now().getYear();
    private final int anoDestino = anoCorrente + 1;

    @BeforeEach
    void setUp() {
        // Janela aberta o ano todo, para isolar regra de janela em testes específicos
        service = new MatriculaService(matriculaRepository, matriculaMapper,
                turmaRepository, alunoRepository, "01-01", "12-31");

        lenient().when(matriculaMapper.toEntity(any(MatriculaRequest.class)))
                .thenAnswer(inv -> {
                    MatriculaRequest r = inv.getArgument(0);
                    Matricula m = new Matricula();
                    m.setAnoLetivo(r.anoLetivo());
                    m.setStatus(r.status());
                    return m;
                });
        lenient().when(matriculaMapper.toResponse(any(Matricula.class)))
                .thenReturn(new MatriculaResponse(
                        1L, anoCorrente, StatusMatricula.ATIVA,
                        10L, null, null, 20L, null, null, null));
        lenient().when(matriculaRepository.save(any(Matricula.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        lenient().when(alunoRepository.save(any(Aluno.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    private Aluno aluno(Long id) {
        Aluno a = new Aluno();
        a.setId(id);
        a.setNome("Maria");
        a.setCpf("12345678900");
        return a;
    }

    private Turma turma(Long id, int ano) {
        Turma t = new Turma();
        t.setId(id);
        t.setNome("3A");
        t.setCurso("3º Ano");
        t.setTurno("Manhã");
        t.setAnoLetivo(ano);
        return t;
    }

    // ─── save (nova matrícula) ───────────────────────────────────────────

    @Test
    void save_camipoFeliz_persisteMatricula() {
        Aluno a = aluno(10L);
        Turma t = turma(20L, anoCorrente);
        when(alunoRepository.findById(10L)).thenReturn(Optional.of(a));
        when(turmaRepository.findById(20L)).thenReturn(Optional.of(t));
        when(matriculaRepository.existsAtivaPorAlunoEAnoLetivoTurma(
                10L, anoCorrente, StatusMatricula.ATIVA)).thenReturn(false);

        MatriculaResponse resp = service.save(new MatriculaRequest(
                anoCorrente, StatusMatricula.ATIVA, 20L, 10L));

        assertThat(resp).isNotNull();
        verify(matriculaRepository).save(any(Matricula.class));
    }

    @Test
    void save_anoPassado_lancaRegraDeNegocio() {
        assertThatThrownBy(() -> service.save(new MatriculaRequest(
                anoCorrente - 1, StatusMatricula.ATIVA, 20L, 10L)))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("ano letivo corrente");
    }

    @Test
    void save_anoFuturo_lancaRegraDeNegocio() {
        assertThatThrownBy(() -> service.save(new MatriculaRequest(
                anoCorrente + 1, StatusMatricula.ATIVA, 20L, 10L)))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("ano letivo corrente");
    }

    @Test
    void save_alunoJaAtivoNoAnoCorrente_lancaRegraDeNegocio() {
        when(alunoRepository.findById(10L)).thenReturn(Optional.of(aluno(10L)));
        when(matriculaRepository.existsAtivaPorAlunoEAnoLetivoTurma(
                10L, anoCorrente, StatusMatricula.ATIVA)).thenReturn(true);

        assertThatThrownBy(() -> service.save(new MatriculaRequest(
                anoCorrente, StatusMatricula.ATIVA, 20L, 10L)))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("já está ativo");
    }

    @Test
    void save_turmaDeOutroAno_lancaRegraDeNegocio() {
        when(alunoRepository.findById(10L)).thenReturn(Optional.of(aluno(10L)));
        when(matriculaRepository.existsAtivaPorAlunoEAnoLetivoTurma(
                10L, anoCorrente, StatusMatricula.ATIVA)).thenReturn(false);
        when(turmaRepository.findById(20L))
                .thenReturn(Optional.of(turma(20L, anoCorrente - 1)));

        assertThatThrownBy(() -> service.save(new MatriculaRequest(
                anoCorrente, StatusMatricula.ATIVA, 20L, 10L)))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Turma destino pertence");
    }

    // ─── rematricular ────────────────────────────────────────────────────

    @Test
    void rematricular_camipoFeliz_preencheBlocoNoAluno() {
        Aluno a = aluno(10L);
        Turma t = turma(30L, anoDestino);
        when(alunoRepository.findById(10L)).thenReturn(Optional.of(a));
        when(turmaRepository.findById(30L)).thenReturn(Optional.of(t));
        when(matriculaRepository.findByAlunoIdAndAnoLetivo(10L, anoDestino))
                .thenReturn(Optional.empty());

        service.rematricular(new RematriculaRequest(10L, 30L));

        RematriculaInfo info = a.getRematriculado();
        assertThat(info).isNotNull();
        assertThat(info.getAnoLetivo()).isEqualTo(anoDestino);
        assertThat(info.getSerie()).isEqualTo("3º Ano");
        assertThat(info.getTurma()).isEqualTo("3A");
        assertThat(info.getTurno()).isEqualTo("Manhã");
        verify(alunoRepository).save(a);
        verify(matriculaRepository).save(any(Matricula.class));
    }

    @Test
    void rematricular_foraDaJanela_lancaRegraDeNegocio() {
        // Janela 1 dia antes de hoje, 1 dia antes de hoje => fechada
        DateTimeFormatter md = DateTimeFormatter.ofPattern("MM-dd");
        MonthDay ontem = MonthDay.from(LocalDate.now().minusDays(2));
        String dia = ontem.format(md);
        MatriculaService svc = new MatriculaService(matriculaRepository, matriculaMapper,
                turmaRepository, alunoRepository, dia, dia);

        assertThatThrownBy(() -> svc.rematricular(new RematriculaRequest(10L, 30L)))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Rematrícula só é permitida");
    }

    @Test
    void rematricular_jaPossuiNoAnoDestino_lancaRegraDeNegocio() {
        Aluno a = aluno(10L);
        when(alunoRepository.findById(10L)).thenReturn(Optional.of(a));
        when(turmaRepository.findById(30L)).thenReturn(Optional.of(turma(30L, anoDestino)));
        when(matriculaRepository.findByAlunoIdAndAnoLetivo(10L, anoDestino))
                .thenReturn(Optional.of(new Matricula()));

        assertThatThrownBy(() -> service.rematricular(new RematriculaRequest(10L, 30L)))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("já possui rematrícula");
    }

    // ─── editar / cancelar rematrícula ──────────────────────────────────

    @Test
    void editarRematricula_atualizaTurmaEBloco() {
        Aluno a = aluno(10L);
        a.setRematriculado(new RematriculaInfo(anoDestino, "2º Ano", "2B", "Tarde"));
        Matricula m = new Matricula();
        m.setAluno(a);
        m.setAnoLetivo(anoDestino);

        when(alunoRepository.findById(10L)).thenReturn(Optional.of(a));
        when(matriculaRepository.findByAlunoIdAndAnoLetivo(10L, anoDestino))
                .thenReturn(Optional.of(m));
        when(turmaRepository.findById(30L))
                .thenReturn(Optional.of(turma(30L, anoDestino)));

        service.editarRematricula(10L, new RematriculaRequest(10L, 30L));

        assertThat(a.getRematriculado().getTurma()).isEqualTo("3A");
        assertThat(a.getRematriculado().getTurno()).isEqualTo("Manhã");
        verify(matriculaRepository).save(m);
    }

    @Test
    void cancelarRematricula_apagaMatriculaELimpaBloco() {
        Aluno a = aluno(10L);
        a.setRematriculado(new RematriculaInfo(anoDestino, "3º Ano", "3A", "Manhã"));
        Matricula m = new Matricula();
        m.setAluno(a);
        m.setAnoLetivo(anoDestino);

        when(alunoRepository.findById(10L)).thenReturn(Optional.of(a));
        when(matriculaRepository.findByAlunoIdAndAnoLetivo(10L, anoDestino))
                .thenReturn(Optional.of(m));

        service.cancelarRematricula(10L);

        assertThat(a.getRematriculado()).isNull();
        verify(matriculaRepository).delete(m);
        verify(alunoRepository).save(a);
    }

    // ─── janela ──────────────────────────────────────────────────────────

    @Test
    void getJanelaRematricula_retornaAnosCorretos() {
        var janela = service.getJanelaRematricula();
        assertThat(janela.disponivel()).isTrue();
        assertThat(janela.anoLetivoOrigem()).isEqualTo(anoCorrente);
        assertThat(janela.anoLetivoDestino()).isEqualTo(anoDestino);
    }
}
