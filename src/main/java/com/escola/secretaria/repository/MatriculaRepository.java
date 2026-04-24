package com.escola.secretaria.repository;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Matricula;
import com.escola.secretaria.domain.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    List<Matricula> findByAluno(Aluno aluno);
    List<Matricula> findByTurma(Turma turma);
    Optional<Matricula> findTopByAlunoIdOrderByIdDesc(Long alunoId);
}