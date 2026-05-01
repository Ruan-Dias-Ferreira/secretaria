package com.escola.secretaria.service;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Frequencia;
import com.escola.secretaria.domain.Matricula;
import com.escola.secretaria.domain.Usuario;
import com.escola.secretaria.domain.enums.Role;
import com.escola.secretaria.domain.enums.StatusMatricula;
import com.escola.secretaria.dto.request.FrequenciaRequest;
import com.escola.secretaria.dto.response.FrequenciaResponse;
import com.escola.secretaria.exception.AcessoNegadoDisciplinaException;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.mapper.FrequenciaMapper;
import com.escola.secretaria.repository.AlunoRepository;
import com.escola.secretaria.repository.DisciplinaRepository;
import com.escola.secretaria.repository.EventoRepository;
import com.escola.secretaria.repository.FrequenciaRepository;
import com.escola.secretaria.repository.MatriculaRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class FrequenciaService {
    private final FrequenciaRepository frequenciaRepository;
    private final FrequenciaMapper frequenciaMapper;
    private final AlunoRepository alunoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final MatriculaRepository matriculaRepository;
    private final EventoRepository eventoRepository;

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void validarAcessoDisciplina(Disciplina disciplina) {
        Usuario usuario = getUsuarioLogado();
        if (usuario.getRole() == Role.PROFESSOR) {
            if (disciplina.getProfessor() == null || !disciplina.getProfessor().getId().equals(usuario.getId())) {
                throw new AcessoNegadoDisciplinaException(
                        "Professor não tem permissão para acessar a disciplina Id: " + disciplina.getId());
            }
        }
    }

    private void validarDataLetiva(LocalDate data) {
        if (data == null) throw new IllegalArgumentException("Data obrigatória.");
        DayOfWeek dow = data.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Data não letiva (fim de semana): " + data);
        }
        if (eventoRepository.existsByData(data)) {
            throw new IllegalArgumentException("Data com evento cadastrado (feriado/coletivo): " + data);
        }
    }

    private void validarAnoLetivoEditavel(LocalDate data) {
        int anoAtual = LocalDate.now().getYear();
        if (data.getYear() < anoAtual) {
            throw new IllegalArgumentException(
                    "Ano letivo encerrado. Não é possível editar frequência de " + data.getYear() + ".");
        }
    }

    public List<Frequencia> findByAluno(Aluno aluno) {
        return frequenciaRepository.findByAluno(aluno);
    }

    @Transactional(readOnly = true)
    public List<FrequenciaResponse> findAll() {
        return frequenciaRepository.findAll().stream().map(frequenciaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FrequenciaResponse findById(Long id) {
        return frequenciaRepository.findById(id)
                .map(frequenciaMapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Frequência não encontrada. Id: " + id));
    }

    @Transactional(readOnly = true)
    public List<FrequenciaResponse> findByTurmaAndData(Long turmaId, LocalDate data) {
        List<Long> alunoIds = matriculaRepository.findByTurmaId(turmaId).stream()
                .map(m -> m.getAluno().getId()).toList();
        if (alunoIds.isEmpty()) return List.of();
        return frequenciaRepository.findByAlunoIdInAndData(alunoIds, data).stream()
                .map(frequenciaMapper::toResponse).toList();
    }

    @Transactional
    public FrequenciaResponse save(FrequenciaRequest request) {
        validarDataLetiva(request.data());
        validarAnoLetivoEditavel(request.data());
        return upsert(request);
    }

    @Transactional
    public List<FrequenciaResponse> saveLote(List<FrequenciaRequest> requests) {
        if (requests == null || requests.isEmpty()) return List.of();
        LocalDate data = requests.get(0).data();
        validarDataLetiva(data);
        validarAnoLetivoEditavel(data);
        return requests.stream().map(this::upsert).toList();
    }

    private FrequenciaResponse upsert(FrequenciaRequest request) {
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + request.alunoId()));
        Disciplina disciplina = null;
        if (request.disciplinaId() != null) {
            disciplina = disciplinaRepository.findById(request.disciplinaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina não encontrada. Id: " + request.disciplinaId()));
            validarAcessoDisciplina(disciplina);
        }

        Frequencia frequencia = frequenciaRepository
                .findByAlunoIdAndData(request.alunoId(), request.data())
                .orElseGet(Frequencia::new);
        frequencia.setData(request.data());
        frequencia.setPresente(request.presente());
        frequencia.setMotivo(request.motivo());
        frequencia.setAluno(aluno);
        frequencia.setDisciplina(disciplina);
        return frequenciaMapper.toResponse(frequenciaRepository.save(frequencia));
    }

    @Transactional
    public FrequenciaResponse update(Long id, FrequenciaRequest request) {
        Frequencia frequencia = frequenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Frequência não encontrada. Id: " + id));
        validarDataLetiva(request.data());
        validarAnoLetivoEditavel(request.data());
        validarAnoLetivoEditavel(frequencia.getData());

        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + request.alunoId()));
        Disciplina disciplina = null;
        if (request.disciplinaId() != null) {
            disciplina = disciplinaRepository.findById(request.disciplinaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina não encontrada. Id: " + request.disciplinaId()));
            validarAcessoDisciplina(disciplina);
        }
        frequenciaMapper.updateEntity(request, frequencia);
        frequencia.setAluno(aluno);
        frequencia.setDisciplina(disciplina);
        return frequenciaMapper.toResponse(frequenciaRepository.save(frequencia));
    }

    @Transactional
    public void delete(Long id) {
        Frequencia frequencia = frequenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Frequência não encontrada. Id: " + id));
        validarAnoLetivoEditavel(frequencia.getData());
        frequenciaRepository.delete(frequencia);
    }

    public record ResumoDia(LocalDate data, long alunosAtivos, long presentes, double percentual) {}

    @Transactional(readOnly = true)
    public ResumoDia resumoDia(LocalDate data) {
        long ativos = matriculaRepository.countByStatus(StatusMatricula.ATIVA);
        long presentes = frequenciaRepository.countByDataAndPresenteTrue(data);
        double pct = ativos > 0 ? (presentes * 100.0) / ativos : 0.0;
        return new ResumoDia(data, ativos, presentes, pct);
    }

    /**
     * Calcula % de presença do aluno no período, considerando apenas dias letivos
     * (exclui sábado, domingo e dias com evento). Retorna -1 se não há dias letivos.
     */
    @Transactional(readOnly = true)
    public double percentualPresenca(Long alunoId, LocalDate inicio, LocalDate fim) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + alunoId));

        long diasLetivos = 0;
        for (LocalDate d = inicio; !d.isAfter(fim); d = d.plusDays(1)) {
            DayOfWeek dow = d.getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) continue;
            if (eventoRepository.existsByData(d)) continue;
            diasLetivos++;
        }
        if (diasLetivos == 0) return -1.0;

        long presencas = frequenciaRepository.findByAlunoAndDataBetween(aluno, inicio, fim).stream()
                .filter(f -> Boolean.TRUE.equals(f.getPresente()))
                .count();
        return (presencas * 100.0) / diasLetivos;
    }
}
