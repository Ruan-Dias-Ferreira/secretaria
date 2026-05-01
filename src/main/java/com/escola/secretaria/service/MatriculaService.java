package com.escola.secretaria.service;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Matricula;
import com.escola.secretaria.domain.RematriculaInfo;
import com.escola.secretaria.domain.Turma;
import com.escola.secretaria.domain.enums.StatusMatricula;
import com.escola.secretaria.dto.request.MatriculaRequest;
import com.escola.secretaria.dto.request.MatriculaStatusRequest;
import com.escola.secretaria.dto.request.RematriculaRequest;
import com.escola.secretaria.dto.response.MatriculaResponse;
import com.escola.secretaria.dto.response.RematriculaJanelaResponse;
import com.escola.secretaria.dto.response.RematriculadoResponse;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.exception.RegraDeNegocioException;
import com.escola.secretaria.mapper.MatriculaMapper;
import com.escola.secretaria.repository.AlunoRepository;
import com.escola.secretaria.repository.MatriculaRepository;
import com.escola.secretaria.repository.TurmaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MatriculaService {

    private static final DateTimeFormatter MD = DateTimeFormatter.ofPattern("MM-dd");

    private final MatriculaRepository matriculaRepository;
    private final MatriculaMapper matriculaMapper;
    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;
    private final MonthDay janelaInicio;
    private final MonthDay janelaFim;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            MatriculaMapper matriculaMapper,
                            TurmaRepository turmaRepository,
                            AlunoRepository alunoRepository,
                            @Value("${secretaria.rematricula.inicio:11-01}") String inicio,
                            @Value("${secretaria.rematricula.fim:12-31}") String fim) {
        this.matriculaRepository = matriculaRepository;
        this.matriculaMapper = matriculaMapper;
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
        this.janelaInicio = MonthDay.parse(inicio, MD);
        this.janelaFim = MonthDay.parse(fim, MD);
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> findAll() {
        return matriculaRepository.findAll().stream()
                .map(matriculaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MatriculaResponse findById(Long id) {
        return matriculaRepository.findById(id)
                .map(matriculaMapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Matrícula não encontrada. Id: " + id));
    }

    @Transactional
    public MatriculaResponse save(MatriculaRequest request) {
        int anoCorrente = LocalDate.now().getYear();
        if (request.anoLetivo() != anoCorrente) {
            throw new RegraDeNegocioException(
                    "Matrícula só é permitida no ano letivo corrente (" + anoCorrente
                            + "). Ano recebido: " + request.anoLetivo());
        }

        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aluno não encontrado. Id: " + request.alunoId()));

        boolean jaTemAtiva = matriculaRepository.existsAtivaPorAlunoEAnoLetivoTurma(
                aluno.getId(), anoCorrente, StatusMatricula.ATIVA);
        if (jaTemAtiva) {
            throw new RegraDeNegocioException(
                    "Aluno já está ativo em uma turma do ano letivo " + anoCorrente);
        }

        Turma turma = turmaRepository.findById(request.turmaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Turma não encontrada. Id: " + request.turmaId()));

        if (turma.getAnoLetivo() != anoCorrente) {
            throw new RegraDeNegocioException(
                    "Turma destino pertence ao ano " + turma.getAnoLetivo()
                            + ", mas matrícula só é permitida no ano corrente " + anoCorrente);
        }

        Matricula matricula = matriculaMapper.toEntity(request);
        matricula.setAluno(aluno);
        matricula.setTurma(turma);

        log.info("Salvando matrícula alunoId={} turmaId={} anoLetivo={}",
                aluno.getId(), turma.getId(), anoCorrente);
        return matriculaMapper.toResponse(matriculaRepository.save(matricula));
    }

    @Transactional
    public MatriculaResponse update(Long id, MatriculaRequest request) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Matrícula não encontrada. Id: " + id));
        matriculaMapper.updateEntity(request, matricula);
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aluno não encontrado. Id: " + request.alunoId()));
        Turma turma = turmaRepository.findById(request.turmaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Turma não encontrada. Id: " + request.turmaId()));
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        return matriculaMapper.toResponse(matriculaRepository.save(matricula));
    }

    @Transactional
    public void delete(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Matrícula não encontrada. Id: " + id));
        matriculaRepository.delete(matricula);
    }

    @Transactional(readOnly = true)
    public Optional<Matricula> findMaisRecentePorAluno(Long alunoId) {
        return matriculaRepository.findTopByAlunoIdOrderByIdDesc(alunoId);
    }

    @Transactional
    public MatriculaResponse updateStatus(Long id, MatriculaStatusRequest request) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Matrícula não encontrada. Id: " + id));
        matricula.setStatus(request.status());
        return matriculaMapper.toResponse(matriculaRepository.save(matricula));
    }

    // ─── Rematrícula ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public RematriculaJanelaResponse getJanelaRematricula() {
        int anoOrigem = LocalDate.now().getYear();
        return new RematriculaJanelaResponse(
                janelaAberta(),
                janelaInicio.atYear(anoOrigem),
                janelaFim.atYear(anoOrigem),
                anoOrigem,
                anoOrigem + 1);
    }

    @Transactional
    public MatriculaResponse rematricular(RematriculaRequest request) {
        validarJanela();
        int anoDestino = LocalDate.now().getYear() + 1;

        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aluno não encontrado. Id: " + request.alunoId()));

        Turma turma = carregarTurmaDestino(request.turmaDestinoId(), anoDestino);

        if (matriculaRepository.findByAlunoIdAndAnoLetivo(aluno.getId(), anoDestino).isPresent()) {
            throw new RegraDeNegocioException(
                    "Aluno já possui rematrícula no ano " + anoDestino);
        }

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setAnoLetivo(anoDestino);
        matricula.setStatus(StatusMatricula.ATIVA);

        aluno.setRematriculado(new RematriculaInfo(
                anoDestino, turma.getCurso(), turma.getNome(), turma.getTurno()));
        alunoRepository.save(aluno);

        log.info("Rematrícula criada alunoId={} turmaDestinoId={} anoDestino={}",
                aluno.getId(), turma.getId(), anoDestino);
        return matriculaMapper.toResponse(matriculaRepository.save(matricula));
    }

    @Transactional
    public MatriculaResponse editarRematricula(Long alunoId, RematriculaRequest request) {
        validarJanela();
        int anoDestino = LocalDate.now().getYear() + 1;

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aluno não encontrado. Id: " + alunoId));

        Matricula matricula = matriculaRepository
                .findByAlunoIdAndAnoLetivo(alunoId, anoDestino)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Aluno não possui rematrícula para editar no ano " + anoDestino));

        Turma turma = carregarTurmaDestino(request.turmaDestinoId(), anoDestino);

        matricula.setTurma(turma);
        aluno.setRematriculado(new RematriculaInfo(
                anoDestino, turma.getCurso(), turma.getNome(), turma.getTurno()));
        alunoRepository.save(aluno);

        log.info("Rematrícula editada alunoId={} novaTurmaId={} anoDestino={}",
                alunoId, turma.getId(), anoDestino);
        return matriculaMapper.toResponse(matriculaRepository.save(matricula));
    }

    @Transactional
    public void cancelarRematricula(Long alunoId) {
        validarJanela();
        int anoDestino = LocalDate.now().getYear() + 1;

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aluno não encontrado. Id: " + alunoId));

        Matricula matricula = matriculaRepository
                .findByAlunoIdAndAnoLetivo(alunoId, anoDestino)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Aluno não possui rematrícula para cancelar no ano " + anoDestino));

        matriculaRepository.delete(matricula);
        aluno.setRematriculado(null);
        alunoRepository.save(aluno);

        log.info("Rematrícula cancelada alunoId={} anoDestino={}", alunoId, anoDestino);
    }

    @Transactional(readOnly = true)
    public List<RematriculadoResponse> findRematriculados() {
        return alunoRepository.findAllRematriculados().stream()
                .filter(a -> a.getRematriculado() != null
                        && a.getRematriculado().getAnoLetivo() != null)
                .map(a -> new RematriculadoResponse(
                        a.getId(), a.getNome(), a.getCpf(),
                        a.getRematriculado().getAnoLetivo(),
                        a.getRematriculado().getSerie(),
                        a.getRematriculado().getTurma(),
                        a.getRematriculado().getTurno()))
                .toList();
    }

    private void validarJanela() {
        if (!janelaAberta()) {
            throw new RegraDeNegocioException(
                    "Rematrícula só é permitida entre " + janelaInicio + " e " + janelaFim
                            + " (fim do 4º bimestre).");
        }
    }

    private boolean janelaAberta() {
        MonthDay hoje = MonthDay.from(LocalDate.now());
        if (!janelaInicio.isAfter(janelaFim)) {
            return !hoje.isBefore(janelaInicio) && !hoje.isAfter(janelaFim);
        }
        return !hoje.isBefore(janelaInicio) || !hoje.isAfter(janelaFim);
    }

    private Turma carregarTurmaDestino(Long turmaId, int anoDestino) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Turma destino não encontrada. Id: " + turmaId));
        if (turma.getAnoLetivo() != anoDestino) {
            throw new RegraDeNegocioException(
                    "Turma destino pertence ao ano " + turma.getAnoLetivo()
                            + ", mas a rematrícula é para " + anoDestino);
        }
        return turma;
    }
}
