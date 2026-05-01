package com.escola.secretaria.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EnderecoDto(
        @NotBlank String rua,
        @NotBlank String bairro,
        @NotBlank String cidade,
        @NotBlank String estado,
        @NotBlank String cep
) {}
