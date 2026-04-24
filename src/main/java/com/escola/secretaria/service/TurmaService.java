package com.escola.secretaria.service;

import com.escola.secretaria.domain.Turma;
import com.escola.secretaria.dto.request.TurmaRequest;
import com.escola.secretaria.dto.response.TurmaResponse;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.mapper.TurmaMapper;
import com.escola.secretaria.repository.TurmaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TurmaService {
    private final TurmaRepository turmaRepository;
    private final TurmaMapper turmaMapper;

    @Transactional(readOnly = true)
    public List<TurmaResponse> findAll(){
        return turmaRepository.findAll()
                .stream()
                .map(t->turmaMapper.toResponse(t))
                .toList();
    }
    @Transactional(readOnly = true)
    public TurmaResponse findById(Long id){
        return turmaRepository.findById(id)
                .map(turmaMapper::toResponse)
                .orElseThrow(()->new RecursoNaoEncontradoException("Turma não encontrada. Id: " + id));
    }
    @Transactional
    public TurmaResponse save(TurmaRequest request){
        Turma turma=turmaMapper.toEntity(request);
        return turmaMapper.toResponse(turmaRepository.save(turma));
    }
    @Transactional
    public TurmaResponse update(Long id, TurmaRequest request){
        Turma turma=turmaRepository.findById(id)
                .orElseThrow(()->new RecursoNaoEncontradoException("Turma não encontrada. Id: " + id));
        turmaMapper.updateEntity(request, turma);
        return turmaMapper.toResponse(turmaRepository.save(turma));
    }
    @Transactional
    public void delete(Long id){
        Turma turma=turmaRepository.findById(id)
                .orElseThrow(()->new RecursoNaoEncontradoException("Turma não encontrada. Id: " + id));
        turmaRepository.delete(turma);
    }
}