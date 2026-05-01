package com.escola.secretaria.repository;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Disciplina;
import com.escola.secretaria.domain.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {
    List<Frequencia> findByAluno(Aluno aluno);
    List<Frequencia> findByAlunoAndDisciplina(Aluno aluno, Disciplina disciplina);
    Optional<Frequencia> findByAlunoIdAndData(Long alunoId, LocalDate data);
    List<Frequencia> findByAlunoIdInAndData(List<Long> alunoIds, LocalDate data);
    List<Frequencia> findByAlunoAndDataBetween(Aluno aluno, LocalDate inicio, LocalDate fim);
    long deleteByData(LocalDate data);
    long countByDataAndPresenteTrue(LocalDate data);
}