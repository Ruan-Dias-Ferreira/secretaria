package com.escola.secretaria.repository;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {
    List<Frequencia> findByAluno(Aluno aluno);
    List<Frequencia> findByAlunoAndDisciplina(Aluno aluno, Disciplina disciplina);
}