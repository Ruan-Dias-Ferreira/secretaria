package com.escola.secretaria.dto.request;

import com.escola.secretaria.domain.enums.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
        @NotBlank
        String login,
        @NotBlank
        String senha,
        @NotNull
        Role role
) {
}
