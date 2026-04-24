package com.escola.secretaria.service;


import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Frequencia;
import com.escola.secretaria.domain.enums.SituacaoFrequencia;
import com.escola.secretaria.dto.request.AlunoRequest;
import com.escola.secretaria.dto.response.AlunoResponse;
import com.escola.secretaria.dto.response.BoletimResponse;
import com.escola.secretaria.dto.response.FrequenciaResumoResponse;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.mapper.AlunoMapper;
import com.escola.secretaria.repository.AlunoRepository;
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
        return alunoRepository.findAll()
                .stream()
                .map(alunoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlunoResponse findById(Long id) {
        return alunoRepository.findById(id)
                .map(alunoMapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + id));
    }

    @Transactional
    public AlunoResponse save(AlunoRequest request) {
        Aluno aluno = alunoMapper.toEntity(request);
        return alunoMapper.toResponse(alunoRepository.save(aluno));
    }

    @Transactional
    public AlunoResponse update(Long id, AlunoRequest request) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + id));
        alunoMapper.updateEntity(request, aluno);
        return alunoMapper.toResponse(alunoRepository.save(aluno));
    }

    @Transactional
    public void delete(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + id));
        alunoRepository.delete(aluno);
    }
}