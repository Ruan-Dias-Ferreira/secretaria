package com.escola.secretaria.repository;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Matricula;
import com.escola.secretaria.domain.Turma;
import com.escola.secretaria.domain.enums.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    List<Matricula> findByAluno(Aluno aluno);

    List<Matricula> findByTurma(Turma turma);

    List<Matricula> findByTurmaId(Long turmaId);

    long countByStatus(StatusMatricula status);

    Optional<Matricula> findTopByAlunoIdOrderByIdDesc(Long alunoId);

    @Query("""
            SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END
              FROM Matricula m
             WHERE m.aluno.id = :alunoId
               AND m.status = :status
               AND m.turma.anoLetivo = :anoLetivo
            """)
    boolean existsAtivaPorAlunoEAnoLetivoTurma(
            @Param("alunoId") Long alunoId,
            @Param("anoLetivo") int anoLetivo,
            @Param("status") StatusMatricula status);

    @Query("""
            SELECT m FROM Matricula m
             WHERE m.aluno.id = :alunoId
               AND m.anoLetivo = :anoLetivo
            """)
    Optional<Matricula> findByAlunoIdAndAnoLetivo(
            @Param("alunoId") Long alunoId,
            @Param("anoLetivo") int anoLetivo);
}
