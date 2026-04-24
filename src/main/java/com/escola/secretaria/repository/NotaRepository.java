package com.escola.secretaria.repository;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByAluno(Aluno aluno);
    List<Nota> findByAlunoAndDisciplina(Aluno aluno, Disciplina disciplina);
}