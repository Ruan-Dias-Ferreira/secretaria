package com.escola.secretaria.controller;

import com.escola.secretaria.dto.request.MatriculaRequest;
import com.escola.secretaria.dto.request.MatriculaStatusRequest;
import com.escola.secretaria.dto.response.MatriculaResponse;
import com.escola.secretaria.service.MatriculaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('SECRETARIA')")
@RequestMapping("/matricula")
@AllArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    @GetMapping
    public ResponseEntity<List<MatriculaResponse>> findAll() {
        return ResponseEntity.ok(matriculaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatriculaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MatriculaResponse> save(@RequestBody @Valid MatriculaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatriculaResponse> update(@PathVariable Long id, @RequestBody @Valid MatriculaRequest request) {
        return ResponseEntity.ok(matriculaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matriculaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SECRETARIA')")
    public ResponseEntity<MatriculaResponse> updateStatus(@PathVariable Long id, @RequestBody MatriculaStatusRequest request) {
        return ResponseEntity.ok(matriculaService.updateStatus(id, request));
    }
}