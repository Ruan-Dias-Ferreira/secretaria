package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.CertidaoNascimento;
import com.escola.secretaria.domain.Endereco;
import com.escola.secretaria.domain.Responsavel;

import java.time.LocalDate;

public record AlunoDetalheResponse(
        Long id,
        String nome,
        String cpf,
        String rg,
        String tituloEleitor,
        LocalDate dataNascimento,
        String email,
        String telefone,
        String telefoneResponsavel,
        String localNascimento,
        String nacionalidade,
        Endereco endereco,
        CertidaoNascimento certidaoNascimento,
        Responsavel mae,
        Responsavel pai,
        Responsavel responsavelLegal
) {}
