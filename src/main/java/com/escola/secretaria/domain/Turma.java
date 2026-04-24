package com.escola.secretaria.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="turma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Turma {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private int anoLetivo;
    private String turno;
    private String curso;
}
