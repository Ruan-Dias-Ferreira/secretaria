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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MatriculaService {
    private final MatriculaRepository matriculaRepository;
    private final MatriculaMapper matriculaMapper;
    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;

    @Transactional(readOnly = true)
    public List<MatriculaResponse> findAll(){
    return matriculaRepository.findAll()
            .stream()
            .map(m->matriculaMapper.toResponse(m))
            .toList();
    }
    @Transactional(readOnly = true)
    public MatriculaResponse findById(Long id){
        return matriculaRepository.findById(id)
                .map(m->matriculaMapper.toResponse(m))
                .orElseThrow(()->new RecursoNaoEncontradoException("Matrícula não encontrada. Id: " + id));
    }
    @Transactional
    public MatriculaResponse save(MatriculaRequest request){
        Matricula matricula=matriculaMapper.toEntity(request);
        Aluno aluno=alunoRepository.findById(request.alunoId())
                        .orElseThrow(()->new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + request.alunoId()));
        boolean jaTemAtiva = matriculaRepository.findByAluno(aluno).stream()
                .anyMatch(m -> m.getStatus() == StatusMatricula.ATIVA
                        && m.getAnoLetivo() == request.anoLetivo());
        if (jaTemAtiva) {
            throw new RegraDeNegocioException(
                    "Aluno já possui matrícula ativa neste ano letivo");
        }
        Turma turma=turmaRepository.findById(request.turmaId())
                        .orElseThrow(()->new RecursoNaoEncontradoException("Turma não encontrada. Id: " + request.turmaId()));
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        return matriculaMapper.toResponse(matriculaRepository.save(matricula));
    }
    @Transactional
    public MatriculaResponse update(Long id,MatriculaRequest request){
        Matricula matricula=matriculaRepository.findById(id)
                .orElseThrow(()->new RecursoNaoEncontradoException("Matrícula não encontrada. Id: " + id));
        matriculaMapper.updateEntity(request, matricula);
        Aluno aluno=alunoRepository.findById(request.alunoId())
                .orElseThrow(()->new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + request.alunoId()));
        Turma turma=turmaRepository.findById(request.turmaId())
                .orElseThrow(()->new RecursoNaoEncontradoException("Turma não encontrada. Id: " + request.turmaId()));
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        return matriculaMapper.toResponse(matriculaRepository.save(matricula));
    }
    @Transactional
    public void delete(Long id){
        Matricula matricula=matriculaRepository.findById(id)
                .orElseThrow(()->new RecursoNaoEncontradoException("Matrícula não encontrada. Id: " + id));
        matriculaRepository.delete(matricula);
    }

    @Transactional(readOnly = true)
    public Optional<Matricula> findMaisRecentePorAluno(Long alunoId) {
        return matriculaRepository.findTopByAlunoIdOrderByIdDesc(alunoId);
    }

    @Transactional
    public MatriculaResponse updateStatus(Long id, MatriculaStatusRequest request) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matrícula não encontrada. Id: " + id));
        matricula.setStatus(request.status());
        return matriculaMapper.toResponse(matriculaRepository.save(matricula));
    }
}
