package com.escola.secretaria.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            RecursoNaoEncontradoException ex, HttpServletRequest request) {
        log.warn("Recurso não encontrado: {} | path: {}", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                LocalDateTime.now(), 404, "Not Found",
                ex.getMessage(), request.getRequestURI(), List.of()
        ));
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErrorResponse> handleRegraDeNegocio(
            RegraDeNegocioException ex, HttpServletRequest request) {
        log.warn("Regra de negócio violada: {} | path: {}", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
                LocalDateTime.now(), 400, "Bad Request",
                ex.getMessage(), request.getRequestURI(), List.of()
        ));
    }

    @ExceptionHandler(AcessoNegadoDisciplinaException.class)
    public ResponseEntity<ErrorResponse> handleAcessoNegado(
            AcessoNegadoDisciplinaException ex, HttpServletRequest request) {
        log.warn("Acesso negado à disciplina: {} | path: {}", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                LocalDateTime.now(), 403, "Forbidden",
                ex.getMessage(), request.getRequestURI(), List.of()
        ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Acesso negado pelo Spring Security | path: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                LocalDateTime.now(), 403, "Forbidden",
                "Você não tem permissão para acessar este recurso",
                request.getRequestURI(), List.of()
        ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Credenciais inválidas | path: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                LocalDateTime.now(), 401, "Unauthorized",
                "Credenciais inválidas", request.getRequestURI(), List.of()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.warn("Erro de validação: {} | path: {}", errors, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
                LocalDateTime.now(), 400, "Bad Request",
                "Erro de validação", request.getRequestURI(), errors
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Erro interno não tratado | path: {} | causa: ", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
                LocalDateTime.now(), 500, "Internal Server Error",
                "Erro interno — contate o suporte", request.getRequestURI(), List.of()
        ));
    }
}
