package com.escola.secretaria.domain;


import com.escola.secretaria.domain.enums.SituacaoNota;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="nota")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Nota {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int bimestre;
    @Column(columnDefinition = "NUMERIC(4,2)")
    private double valor;
    @Enumerated(EnumType.STRING)
    private SituacaoNota situacao;
    @ManyToOne @JoinColumn(name = "aluno_id")
    private Aluno aluno;
    @ManyToOne @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;
}
