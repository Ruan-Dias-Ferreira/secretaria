package com.escola.secretaria.service;

import com.escola.secretaria.domain.Turma;
import com.escola.secretaria.dto.request.TurmaRequest;
import com.escola.secretaria.dto.response.TurmaResponse;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.exception.RegraDeNegocioException;
import com.escola.secretaria.mapper.TurmaMapper;
import com.escola.secretaria.repository.TurmaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TurmaService {
    private final TurmaRepository turmaRepository;
    private final TurmaMapper turmaMapper;
    private final MatriculaService matriculaService;

    @Transactional(readOnly = true)
    public List<TurmaResponse> findAll() {
        return turmaRepository.findAll()
                .stream()
                .map(turmaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TurmaResponse findById(Long id) {
        return turmaRepository.findById(id)
                .map(turmaMapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma não encontrada. Id: " + id));
    }

    @Transactional
    public TurmaResponse save(TurmaRequest request) {
        boolean rematricula = validarAnoCadastro(request.anoLetivo());
        Turma turma = turmaMapper.toEntity(request);
        turma.setRematricula(rematricula);
        return turmaMapper.toResponse(turmaRepository.save(turma));
    }

    @Transactional
    public TurmaResponse update(Long id, TurmaRequest request) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma não encontrada. Id: " + id));
        assertEditavel(turma);
        if (turma.getAnoLetivo() != request.anoLetivo()) {
            boolean rematricula = validarAnoCadastro(request.anoLetivo());
            turma.setRematricula(rematricula);
        }
        turmaMapper.updateEntity(request, turma);
        return turmaMapper.toResponse(turmaRepository.save(turma));
    }

    @Transactional
    public void delete(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma não encontrada. Id: " + id));
        assertEditavel(turma);
        turmaRepository.delete(turma);
    }

    /**
     * Bloqueia mutações em turmas de anos encerrados. Turmas do ano em andamento
     * são editáveis. Turmas de rematrícula (ano+1) só são editáveis durante a
     * janela aberta de rematrícula.
     */
    private void assertEditavel(Turma turma) {
        int anoAtual = LocalDate.now().getYear();
        if (turma.getAnoLetivo() < anoAtual) {
            throw new RegraDeNegocioException(
                    "Ano letivo " + turma.getAnoLetivo() + " encerrado. "
                            + "Não é possível modificar turmas de anos passados.");
        }
        if (turma.getAnoLetivo() > anoAtual && !matriculaService.janelaAberta()) {
            throw new RegraDeNegocioException(
                    "Turmas do ano " + turma.getAnoLetivo()
                            + " só podem ser modificadas durante a janela de rematrícula.");
        }
    }

    /**
     * Retorna true se turma deve ser marcada como turma de rematrícula (ano+1).
     * Lança {@link RegraDeNegocioException} para anos inválidos.
     */
    private boolean validarAnoCadastro(int anoLetivo) {
        int anoAtual = LocalDate.now().getYear();
        if (anoLetivo < anoAtual) {
            throw new RegraDeNegocioException(
                    "Ano letivo " + anoLetivo + " encerrado. Cadastro permitido apenas no ano em andamento ("
                            + anoAtual + ") ou no ano seguinte durante a janela de rematrícula.");
        }
        if (anoLetivo == anoAtual) {
            return false;
        }
        if (anoLetivo == anoAtual + 1) {
            if (!matriculaService.janelaAberta()) {
                throw new RegraDeNegocioException(
                        "Cadastro de turmas para " + anoLetivo
                                + " só é permitido durante o período de rematrícula.");
            }
            return true;
        }
        throw new RegraDeNegocioException(
                "Ano letivo " + anoLetivo + " inválido. Permitido: " + anoAtual + " ou " + (anoAtual + 1) + ".");
    }
}
