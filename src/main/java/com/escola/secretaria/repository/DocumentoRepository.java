package com.escola.secretaria.repository;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Documento;
import com.escola.secretaria.domain.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByAluno(Aluno aluno);
    List<Documento> findByAlunoId(Long alunoId);
    Optional<Documento> findByAlunoIdAndTipo(Long alunoId, TipoDocumento tipo);
}