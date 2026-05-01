package com.escola.secretaria.repository;

import com.escola.secretaria.domain.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByCpf(String cpf);

    @Query("""
            SELECT a FROM Aluno a
             WHERE a.rematriculado.anoLetivo IS NOT NULL
             ORDER BY a.nome
            """)
    List<Aluno> findAllRematriculados();
}
