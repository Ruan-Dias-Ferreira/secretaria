package com.escola.secretaria.controller;

import com.escola.secretaria.dto.request.AlunoRequest;
import com.escola.secretaria.dto.response.AlunoResponse;
import com.escola.secretaria.dto.response.FrequenciaResumoResponse;
import com.escola.secretaria.service.AlunoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('SECRETARIA')")
@RequestMapping("/aluno")
@AllArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping("/{id}/frequencias")
    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    public ResponseEntity<List<FrequenciaResumoResponse>> getFrequencias(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.getFrequenciasPorAluno(id));
    }

    @GetMapping
    public ResponseEntity<List<AlunoResponse>> findAll() {
        return ResponseEntity.ok(alunoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> save(@RequestBody @Valid AlunoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponse> update(@PathVariable Long id,
            @RequestBody @Valid AlunoRequest request) {
        return ResponseEntity.ok(alunoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alunoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
