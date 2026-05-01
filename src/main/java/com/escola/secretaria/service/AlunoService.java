package com.escola.secretaria.service;


import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Frequencia;
import com.escola.secretaria.domain.enums.SituacaoFrequencia;
import com.escola.secretaria.dto.request.AlunoRequest;
import com.escola.secretaria.domain.Matricula;
import com.escola.secretaria.domain.enums.StatusMatricula;
import com.escola.secretaria.dto.response.AlunoDetalheResponse;
import com.escola.secretaria.dto.response.AlunoResponse;
import com.escola.secretaria.dto.response.AlunoSituacaoResponse;
import com.escola.secretaria.dto.response.BoletimResponse;
import com.escola.secretaria.dto.response.FrequenciaResumoResponse;
import com.escola.secretaria.dto.response.MatriculaResponse;
import com.escola.secretaria.dto.response.PendenciasResumoResponse;
import com.escola.secretaria.domain.enums.TipoDocumento;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.mapper.AlunoMapper;
import com.escola.secretaria.mapper.MatriculaMapper;
import com.escola.secretaria.repository.AlunoRepository;
import com.escola.secretaria.repository.DocumentoRepository;
import com.escola.secretaria.repository.MatriculaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final AlunoMapper alunoMapper;
    private final NotaService notaService;
    private final FrequenciaService frequenciaService;
    private final MatriculaRepository matriculaRepository;
    private final MatriculaMapper matriculaMapper;
    private final DocumentoRepository documentoRepository;

    //Frequencia
    @Transactional(readOnly = true)
    public List<FrequenciaResumoResponse> getFrequenciasPorAluno(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + id));
        List<Frequencia> frequencias = frequenciaService.findByAluno(aluno);
        return frequencias.stream()
                .collect(Collectors.groupingBy(Frequencia::getDisciplina))
                .entrySet().stream()
                .map(entry -> {
                    Disciplina disciplina = entry.getKey();
                    List<Frequencia> freqDaDisciplina = entry.getValue();
                    long presencas = freqDaDisciplina.stream()
                            .filter(f -> Boolean.TRUE.equals(f.getPresente()))
                            .count();
                    double percentual = (presencas / (double) disciplina.getCargaHoraria()) * 100.0;
                    SituacaoFrequencia situacao = percentual >= 75.0
                            ? SituacaoFrequencia.REGULAR
                            : SituacaoFrequencia.REPROVADO_FREQUENCIA;
                    return new FrequenciaResumoResponse(
                            disciplina.getId(),
                            disciplina.getNome(),
                            disciplina.getCargaHoraria(),
                            (int) presencas,
                            percentual,
                            situacao
                    );
                })
                .toList();
    }
    //Boletim
    @Transactional(readOnly = true)
    public List<BoletimResponse> getBoletim(Long id) {
        return notaService.getBoletim(id);
    }
    @Transactional(readOnly = true)
    public List<AlunoResponse> findAll() {
        return findAll(null);
    }

    @Transactional(readOnly = true)
    public List<AlunoResponse> findAll(String search) {
        List<Aluno> all = alunoRepository.findAll();
        if (search == null || search.isBlank()) {
            return all.stream().map(alunoMapper::toResponse).toList();
        }
        String q = search.trim().toLowerCase();
        String qDigits = q.replaceAll("\\D", "");
        boolean isCpf = !qDigits.isEmpty() && qDigits.length() >= 3
                && q.replaceAll("[\\d.\\-\\s]", "").isEmpty();
        return all.stream()
                .filter(a -> matches(a, q, qDigits, isCpf))
                .sorted((a, b) -> Integer.compare(rank(a, q), rank(b, q)))
                .map(alunoMapper::toResponse)
                .toList();
    }

    private boolean matches(Aluno a, String q, String qDigits, boolean isCpf) {
        if (isCpf) {
            String cpf = a.getCpf() == null ? "" : a.getCpf().replaceAll("\\D", "");
            return cpf.contains(qDigits);
        }
        String nome = a.getNome() == null ? "" : a.getNome().toLowerCase();
        return nome.contains(q);
    }

    private int rank(Aluno a, String q) {
        String nome = a.getNome() == null ? "" : a.getNome().toLowerCase();
        String first = nome.split("\\s+")[0];
        if (first.equals(q)) return 0;
        if (first.startsWith(q)) return 1;
        if (nome.startsWith(q)) return 2;
        return 3;
    }

    @Transactional(readOnly = true)
    public AlunoDetalheResponse findById(Long id) {
        return alunoRepository.findById(id)
                .map(alunoMapper::toDetalheResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + id));
    }

    @Transactional
    public AlunoResponse save(AlunoRequest request) {
        Aluno aluno = alunoMapper.toEntity(request);
        normalizar(aluno);
        return alunoMapper.toResponse(alunoRepository.save(aluno));
    }

    private void normalizar(Aluno a) {
        if (a.getEmail() != null && a.getEmail().isBlank()) a.setEmail(null);
        if (a.getRg() != null && a.getRg().isBlank()) a.setRg(null);
        if (a.getTituloEleitor() != null && a.getTituloEleitor().isBlank()) a.setTituloEleitor(null);
        if (a.getTelefone() != null && a.getTelefone().isBlank()) a.setTelefone(null);
        if (a.getMae() != null && isResponsavelVazio(a.getMae())) a.setMae(null);
        if (a.getPai() != null && isResponsavelVazio(a.getPai())) a.setPai(null);
        if (a.getResponsavelLegal() != null && isResponsavelVazio(a.getResponsavelLegal())) a.setResponsavelLegal(null);
    }

    private boolean isResponsavelVazio(com.escola.secretaria.domain.Responsavel r) {
        return blank(r.getNome()) && blank(r.getCpf()) && blank(r.getRg())
                && blank(r.getTituloEleitor()) && blank(r.getTelefone());
    }

    private boolean blank(String s) { return s == null || s.isBlank(); }

    @Transactional
    public AlunoResponse update(Long id, AlunoRequest request) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + id));
        alunoMapper.updateEntity(request, aluno);
        normalizar(aluno);
        return alunoMapper.toResponse(alunoRepository.save(aluno));
    }

    @Transactional(readOnly = true)
    public AlunoSituacaoResponse getSituacao(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + id));
        List<Matricula> matriculas = matriculaRepository.findByAluno(aluno);
        Matricula atual = matriculas.stream()
                .filter(m -> m.getStatus() == StatusMatricula.ATIVA)
                .max((a, b) -> Integer.compare(a.getAnoLetivo(), b.getAnoLetivo()))
                .orElse(matriculas.stream()
                        .max((a, b) -> Integer.compare(a.getAnoLetivo(), b.getAnoLetivo()))
                        .orElse(null));

        Double percentualFreq = null;
        try {
            List<FrequenciaResumoResponse> resumo = getFrequenciasPorAluno(id);
            if (!resumo.isEmpty()) {
                percentualFreq = resumo.stream()
                        .mapToDouble(r -> r.percentual() == null ? 0.0 : r.percentual())
                        .average().orElse(0.0);
            }
        } catch (Exception ignored) {}

        int ativas = (int) matriculas.stream()
                .filter(m -> m.getStatus() == StatusMatricula.ATIVA).count();

        List<MatriculaResponse> historico = matriculas.stream()
                .sorted((a, b) -> Integer.compare(b.getAnoLetivo(), a.getAnoLetivo()))
                .map(matriculaMapper::toResponse)
                .toList();

        return new AlunoSituacaoResponse(
                aluno.getId(),
                aluno.getNome(),
                atual != null ? atual.getStatus() : null,
                atual != null ? atual.getAnoLetivo() : null,
                atual != null && atual.getTurma() != null ? atual.getTurma().getId() : null,
                atual != null && atual.getTurma() != null ? atual.getTurma().getNome() : null,
                atual != null && atual.getTurma() != null ? atual.getTurma().getCurso() : null,
                atual != null && atual.getTurma() != null ? atual.getTurma().getTurno() : null,
                percentualFreq,
                matriculas.size(),
                ativas,
                historico
        );
    }

    @Transactional(readOnly = true)
    public PendenciasResumoResponse getPendenciasResumo() {
        List<Aluno> all = alunoRepository.findAll();
        int totalTipos = TipoDocumento.values().length;
        long docsPend = all.stream()
                .filter(a -> documentoRepository.findByAlunoId(a.getId()).size() < totalTipos)
                .count();
        long infoPend = all.stream().filter(this::temInfoPendente).count();
        return new PendenciasResumoResponse(docsPend, infoPend, all.size());
    }

    private boolean temInfoPendente(Aluno a) {
        if (blank(a.getRg()) || blank(a.getTituloEleitor()) || blank(a.getTelefone())
                || blank(a.getLocalNascimento()) || blank(a.getNacionalidade())) return true;
        if (a.getEndereco() == null
                || blank(a.getEndereco().getRua()) || blank(a.getEndereco().getBairro())
                || blank(a.getEndereco().getCidade()) || blank(a.getEndereco().getEstado())
                || blank(a.getEndereco().getCep())) return true;
        boolean temResp = (a.getMae() != null && !blank(a.getMae().getNome()))
                || (a.getPai() != null && !blank(a.getPai().getNome()))
                || (a.getResponsavelLegal() != null && !blank(a.getResponsavelLegal().getNome()));
        if (!temResp) return true;
        return respIncompleto(a.getMae()) && respIncompleto(a.getPai()) && respIncompleto(a.getResponsavelLegal());
    }

    private boolean respIncompleto(com.escola.secretaria.domain.Responsavel r) {
        if (r == null || blank(r.getNome())) return true;
        return blank(r.getCpf()) || blank(r.getTelefone()) || blank(r.getProfissao());
    }

    @Transactional
    public void delete(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + id));
        alunoRepository.delete(aluno);
    }
}