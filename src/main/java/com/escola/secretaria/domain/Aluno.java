package com.escola.secretaria.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="aluno")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aluno {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String rg;
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String telefone;
    private String endereco;
    private String nomeMae;
    private String nomePai;
}
