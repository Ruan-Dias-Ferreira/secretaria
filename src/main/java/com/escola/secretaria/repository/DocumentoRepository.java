package com.escola.secretaria.repository;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByAluno(Aluno aluno);
}