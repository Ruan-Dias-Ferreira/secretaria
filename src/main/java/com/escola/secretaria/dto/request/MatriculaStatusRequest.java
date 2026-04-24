package com.escola.secretaria.dto.request;

import com.escola.secretaria.domain.enums.StatusMatricula;

import jakarta.validation.constraints.NotNull;

public record MatriculaStatusRequest(@NotNull StatusMatricula status) {
}