package com.escola.secretaria.repository;

import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    List<Disciplina> findByTurma(Turma turma);
}