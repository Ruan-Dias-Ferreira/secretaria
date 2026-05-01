package com.escola.secretaria.dto.request;

public record ResponsavelDto(
        String nome,
        String cpf,
        String rg,
        String tituloEleitor,
        String telefone,
        String profissao
) {
    public boolean isVazio() {
        return (nome == null || nome.isBlank())
                && (cpf == null || cpf.isBlank())
                && (rg == null || rg.isBlank())
                && (tituloEleitor == null || tituloEleitor.isBlank())
                && (telefone == null || telefone.isBlank())
                && (profissao == null || profissao.isBlank());
    }
}
