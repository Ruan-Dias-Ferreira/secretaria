package com.escola.secretaria.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="disciplina")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Disciplina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private int cargaHoraria;
    @ManyToOne @JoinColumn(name = "turma_id")
    private Turma turma;
    @ManyToOne @JoinColumn(name = "professor_id")
    private Usuario professor;
}