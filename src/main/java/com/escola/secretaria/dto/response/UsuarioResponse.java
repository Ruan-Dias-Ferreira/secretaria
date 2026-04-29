package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.enums.Role;

public record UsuarioResponse(
        Long id,
        String login,
        Role role
) {
}
