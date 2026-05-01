package com.escola.secretaria.repository;

import com.escola.secretaria.domain.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    boolean existsByAnoLetivo(int anoLetivo);
}