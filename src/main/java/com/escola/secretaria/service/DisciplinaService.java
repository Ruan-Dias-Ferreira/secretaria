package com.escola.secretaria.service;

import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Turma;
import com.escola.secretaria.domain.Usuario;
import com.escola.secretaria.domain.enums.Role;
import com.escola.secretaria.dto.request.DisciplinaRequest;
import com.escola.secretaria.dto.response.DisciplinaResponse;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.exception.RegraDeNegocioException;
import com.escola.secretaria.mapper.DisciplinaMapper;
import com.escola.secretaria.repository.DisciplinaRepository;
import com.escola.secretaria.repository.TurmaRepository;
import com.escola.secretaria.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class DisciplinaService {
    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaMapper disciplinaMapper;
    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<DisciplinaResponse> findAll() {
        return disciplinaRepository.findAll()
                .stream()
                .map(disciplinaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DisciplinaResponse findById(Long id) {
        return disciplinaRepository.findById(id)
                .map(disciplinaMapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina não encontrada. Id: " + id));
    }

    @Transactional
    public DisciplinaResponse save(DisciplinaRequest request) {
        validarProfessor(request.professorId());
        Disciplina disciplina = disciplinaMapper.toEntity(request);
        Turma turma = turmaRepository.findById(request.turmaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma não encontrada. Id: " + request.turmaId()));
        disciplina.setTurma(turma);
        return disciplinaMapper.toResponse(disciplinaRepository.save(disciplina));
    }

    @Transactional
    public DisciplinaResponse update(Long id, DisciplinaRequest request) {
        validarProfessor(request.professorId());
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina não encontrada. Id: " + id));
        disciplinaMapper.updateEntity(request, disciplina);
        return disciplinaMapper.toResponse(disciplinaRepository.save(disciplina));
    }

    @Transactional
    public void delete(Long id) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina não encontrada. Id: " + id));
        disciplinaRepository.delete(disciplina);
    }

    private void validarProfessor(Long professorId) {
        if (professorId == null) return;
        Usuario professor = usuarioRepository.findById(professorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado. Id: " + professorId));
        if (professor.getRole() != Role.PROFESSOR) {
            throw new RegraDeNegocioException("Usuário não possui role PROFESSOR");
        }
    }
}