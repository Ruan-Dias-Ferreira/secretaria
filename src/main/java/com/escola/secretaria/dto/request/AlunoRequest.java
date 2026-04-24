package com.escola.secretaria.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AlunoRequest(
    @NotBlank
    String nome,
    @NotBlank
    String cpf,
    @NotBlank
    String rg,
    @NotNull
    LocalDate dataNascimento,
    @NotBlank
    String email,
    @NotBlank
    String telefone,
    @NotBlank
    String endereco,
    @NotBlank
    String nomeMae,
    String nomePai
    )
{}
