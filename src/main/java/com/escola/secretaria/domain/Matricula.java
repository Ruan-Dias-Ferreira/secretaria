package com.escola.secretaria.domain;

import com.escola.secretaria.domain.enums.StatusMatricula;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="matricula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Matricula {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int anoLetivo;
    @Enumerated(EnumType.STRING)
    private StatusMatricula status;
    @ManyToOne @JoinColumn(name = "turma_id")
    private Turma turma;
    @ManyToOne @JoinColumn(name = "aluno_id")
    private Aluno aluno;
}
