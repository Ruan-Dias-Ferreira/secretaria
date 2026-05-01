package com.escola.secretaria.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "aluno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String rg;
    private String cpf;
    private String tituloEleitor;
    private LocalDate dataNascimento;
    private String email;
    private String telefone;
    private String telefoneResponsavel;

    @Embedded
    private Endereco endereco;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "matricula", column = @Column(name = "certidao_matricula")),
        @AttributeOverride(name = "livro",     column = @Column(name = "certidao_livro")),
        @AttributeOverride(name = "folha",     column = @Column(name = "certidao_folha")),
        @AttributeOverride(name = "termo",     column = @Column(name = "certidao_termo"))
    })
    private CertidaoNascimento certidaoNascimento;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "nome",          column = @Column(name = "mae_nome")),
        @AttributeOverride(name = "cpf",           column = @Column(name = "mae_cpf")),
        @AttributeOverride(name = "rg",            column = @Column(name = "mae_rg")),
        @AttributeOverride(name = "tituloEleitor", column = @Column(name = "mae_titulo_eleitor")),
        @AttributeOverride(name = "telefone",      column = @Column(name = "mae_telefone"))
    })
    private Responsavel mae;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "nome",          column = @Column(name = "pai_nome")),
        @AttributeOverride(name = "cpf",           column = @Column(name = "pai_cpf")),
        @AttributeOverride(name = "rg",            column = @Column(name = "pai_rg")),
        @AttributeOverride(name = "tituloEleitor", column = @Column(name = "pai_titulo_eleitor")),
        @AttributeOverride(name = "telefone",      column = @Column(name = "pai_telefone"))
    })
    private Responsavel pai;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "nome",          column = @Column(name = "legal_nome")),
        @AttributeOverride(name = "cpf",           column = @Column(name = "legal_cpf")),
        @AttributeOverride(name = "rg",            column = @Column(name = "legal_rg")),
        @AttributeOverride(name = "tituloEleitor", column = @Column(name = "legal_titulo_eleitor")),
        @AttributeOverride(name = "telefone",      column = @Column(name = "legal_telefone"))
    })
    private Responsavel responsavelLegal;

    @Embedded
    private RematriculaInfo rematriculado;
}
