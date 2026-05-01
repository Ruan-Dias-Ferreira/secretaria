package com.escola.secretaria.repository;

import com.escola.secretaria.domain.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByDataBetween(LocalDate inicio, LocalDate fim);
    Optional<Evento> findByData(LocalDate data);
    boolean existsByData(LocalDate data);
}
