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
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotaService {
    private final NotaRepository notaRepository;
    private final NotaMapper notaMapper;
    private final AlunoRepository alunoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final MatriculaService matriculaService;

    private boolean isStatusEspecial(StatusMatricula status) {
        return status == StatusMatricula.DESISTENTE || status == StatusMatricula.TRANSFERIDO;
    }

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

    private SituacaoNota calcularSituacaoBimestre(double valor) {
        return valor >= 6.0 ? SituacaoNota.APROVADO : SituacaoNota.RECUPERACAO;
    }

    private SituacaoNota calcularSituacaoFinal(double mediaFinal) {
        return mediaFinal >= 6.0 ? SituacaoNota.APROVADO : SituacaoNota.REPROVADO;
    }
    @Transactional
    public List<BoletimResponse> getBoletim(Long alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + alunoId));

        Optional<Matricula> matriculaOpt = matriculaService.findMaisRecentePorAluno(alunoId);
        if (matriculaOpt.isPresent()) {
            StatusMatricula statusMatricula = matriculaOpt.get().getStatus();
            if (isStatusEspecial(statusMatricula)) {
                SituacaoNota situacaoEspecial = statusMatricula == StatusMatricula.DESISTENTE
                        ? SituacaoNota.DESISTENTE
                        : SituacaoNota.TRANSFERIDO;
                List<Nota> notas = notaRepository.findByAluno(aluno);
                return notas.stream()
                        .collect(Collectors.groupingBy(Nota::getDisciplina))
                        .entrySet().stream()
                        .map(entry -> new BoletimResponse(
                                entry.getKey().getNome(),
                                entry.getValue().stream().map(notaMapper::toResponse).toList(),
                                0.0,
                                situacaoEspecial
                        ))
                        .toList();
            }
        }

        List<Nota> notas = notaRepository.findByAluno(aluno);
        return notas.stream()
                .collect(Collectors.groupingBy(Nota::getDisciplina))
                .entrySet().stream()
                .map(entry -> {
                    Disciplina disciplina = entry.getKey();
                    List<Nota> notasDaDisciplina = entry.getValue();
                    List<NotaResponse> notaResponses = notasDaDisciplina.stream()
                            .map(notaMapper::toResponse)
                            .toList();
                    double media = notasDaDisciplina.stream()
                            .mapToDouble(Nota::getValor)
                            .average()
                            .orElse(0.0);
                    SituacaoNota situacao = calcularSituacaoFinal(media);
                    return new BoletimResponse(
                            disciplina.getNome(),
                            notaResponses,
                            media,
                            situacao
                    );
                })
                .toList();
    }
    //CRUD
    @Transactional(readOnly = true)
    public List<NotaResponse> findAll(){
        return notaRepository.findAll()
                .stream()
                .map(n->notaMapper.toResponse(n))
                .toList();
    }
    @Transactional(readOnly = true)
    public NotaResponse findById(Long id){
        return notaRepository.findById(id)
                .map(n->notaMapper.toResponse(n))
                .orElseThrow(()->new RecursoNaoEncontradoException("Nota não encontrada. Id: " + id));
    }
    @Transactional
    public NotaResponse save(NotaRequest request){
        Nota nota=notaMapper.toEntity(request);
        nota.setSituacao(calcularSituacaoBimestre(request.valor()));
        Aluno aluno=alunoRepository.findById(request.alunoId())
                .orElseThrow(()->new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + request.alunoId()));
        Disciplina disciplina=disciplinaRepository.findById(request.disciplinaId())
                .orElseThrow(()->new RecursoNaoEncontradoException("Disciplina não encontrada. Id: " + request.disciplinaId()));
        validarAcessoDisciplina(disciplina);
        nota.setDisciplina(disciplina);
        nota.setAluno(aluno);
        return  notaMapper.toResponse(notaRepository.save(nota));
    }
    @Transactional
    public NotaResponse update(Long id,NotaRequest request){
        Nota nota=notaRepository.findById(id)
                .orElseThrow(()->new RecursoNaoEncontradoException("Nota não encontrada. Id: " + id));
        notaMapper.updateEntity(request,nota);
        nota.setSituacao(calcularSituacaoBimestre(request.valor()));
        Aluno aluno=alunoRepository.findById(request.alunoId())
                .orElseThrow(()->new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + request.alunoId()));
        Disciplina disciplina=disciplinaRepository.findById(request.disciplinaId())
                .orElseThrow(()->new RecursoNaoEncontradoException("Disciplina não encontrada. Id: " + request.disciplinaId()));
        validarAcessoDisciplina(disciplina);
        nota.setDisciplina(disciplina);
        nota.setAluno(aluno);
        return  notaMapper.toResponse(notaRepository.save(nota));
    }
    @Transactional
    public void delete(Long id){
        Nota nota =notaRepository.findById(id)
                .orElseThrow(()->new RecursoNaoEncontradoException("Nota não encontrada. Id: " + id));
        notaRepository.delete(nota);
    }
}
