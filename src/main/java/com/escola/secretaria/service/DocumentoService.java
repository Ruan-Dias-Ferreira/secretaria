package com.escola.secretaria.service;


import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Documento;
import com.escola.secretaria.dto.request.DocumentoRequest;
import com.escola.secretaria.dto.response.DocumentoResponse;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.mapper.DocumentoMapper;
import com.escola.secretaria.repository.AlunoRepository;
import com.escola.secretaria.repository.DocumentoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class DocumentoService {
    private final DocumentoMapper documentoMapper;
    private final DocumentoRepository documentoRepository;
    private final AlunoRepository alunoRepository;
    @Transactional(readOnly = true)
    public List<DocumentoResponse> findAll() {
        return documentoRepository.findAll()
                .stream()
                .map(d->documentoMapper.toResponse(d))
                .toList();
    }
    @Transactional(readOnly = true)
    public DocumentoResponse findById(Long id) {
        return documentoRepository.findById(id)
                .map(d->documentoMapper.toResponse(d))
                .orElseThrow(()->new RecursoNaoEncontradoException("Documento não encontrado. Id: " + id));
    }
    @Transactional
    public DocumentoResponse save(DocumentoRequest request) {
        Documento documento = documentoMapper.toEntity(request);
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado. Id: " + request.alunoId()));
        documento.setAluno(aluno);
        documento.setDataEmissao(LocalDate.now());
        return documentoMapper.toResponse(documentoRepository.save(documento));
    }
    @Transactional
    public void delete(Long id) {
        Documento documento=documentoRepository.findById(id)
                .orElseThrow(()->new RecursoNaoEncontradoException("Documento não encontrado. Id: " + id));
        documentoRepository.delete(documento);
    }
}
