package com.escola.secretaria.service;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Frequencia;
import com.escola.secretaria.domain.Usuario;
import com.escola.secretaria.domain.enums.Role;
import com.escola.secretaria.dto.request.FrequenciaRequest;
import com.escola.secretaria.dto.response.FrequenciaResponse;
import com.escola.secretaria.exception.AcessoNegadoDisciplinaException;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.mapper.FrequenciaMapper;
import com.escola.secretaria.repository.AlunoRepository;
import com.escola.secretaria.repository.DisciplinaRepository;
import com.escola.secretaria.repository.FrequenciaRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class FrequenciaService {
    private final FrequenciaRepository frequenciaRepository;
    private final FrequenciaMapper frequenciaMapper;
    private final AlunoRepository alunoRepository;
    private final DisciplinaRepository disciplinaRepository;

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

    public List<Frequencia> findByAluno(Aluno aluno) {
        return frequenciaRepository.findByAluno(aluno);
    }
    @Transactional(readOnly = true)
    public List<FrequenciaResponse> findAll() {
        return frequenciaRepository.findAll()
                .stream()
                .map(d->frequenciaMapper.toResponse(d))
                .toList();
    }
    @Transactional(readOnly = true)
    public FrequenciaResponse findById(Long id) {
        return frequenciaRepository.findById(id)
                .map(frequenciaMapper::toResponse)
                .orElseThrow(()->new RecursoNaoEncontradoException("Frequência não encontrada. Id: " + id));
    }
    @Transactional
    public FrequenciaResponse save(FrequenciaRequest request) {
        Frequencia frequencia = frequenciaMapper.toEntity(request);
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(()->new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + request.alunoId()));
        Disciplina disciplina = disciplinaRepository.findById(request.disciplinaId())
                .orElseThrow(()->new RecursoNaoEncontradoException("Disciplina não encontrada. Id: " + request.disciplinaId()));
        validarAcessoDisciplina(disciplina);
        frequencia.setAluno(aluno);
        frequencia.setDisciplina(disciplina);
        return frequenciaMapper.toResponse(frequenciaRepository.save(frequencia));
    }
    @Transactional
    public FrequenciaResponse update(Long id,FrequenciaRequest request) {
    Frequencia frequencia = frequenciaRepository.findById(id)
            .orElseThrow(()->new RecursoNaoEncontradoException("Frequência não encontrada. Id: " + id));
        frequenciaMapper.updateEntity(request, frequencia);
    Aluno aluno=alunoRepository.findById(request.alunoId())
            .orElseThrow(()->new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + request.alunoId()));
    Disciplina  disciplina=disciplinaRepository.findById(request.disciplinaId())
            .orElseThrow(()->new RecursoNaoEncontradoException("Disciplina não encontrada. Id: " + request.disciplinaId()));
    validarAcessoDisciplina(disciplina);
    frequencia.setAluno(aluno);
    frequencia.setDisciplina(disciplina);
    return frequenciaMapper.toResponse(frequenciaRepository.save(frequencia));
    }
    @Transactional
    public void delete(Long id) {
        Frequencia frequencia=frequenciaRepository.findById(id)
                .orElseThrow(()->new  RecursoNaoEncontradoException("Frequência não encontrada. Id: " + id));
        frequenciaRepository.delete(frequencia);
    }
}
