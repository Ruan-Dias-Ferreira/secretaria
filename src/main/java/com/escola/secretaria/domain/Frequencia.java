package com.escola.secretaria.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="frequencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Frequencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate data;
    private Boolean presente;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private MotivoFalta motivo;

    @ManyToOne @JoinColumn(name = "aluno_id")
    private Aluno aluno;
    @ManyToOne @JoinColumn(name="disciplina_id")
    private Disciplina disciplina;
}