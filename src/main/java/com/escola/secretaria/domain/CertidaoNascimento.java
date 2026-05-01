package com.escola.secretaria.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertidaoNascimento {
    private String matricula;
    private String livro;
    private String folha;
    private String termo;
}
