package com.escola.secretaria.controller;

import com.escola.secretaria.dto.request.TurmaRequest;
import com.escola.secretaria.dto.response.TurmaResponse;
import com.escola.secretaria.service.TurmaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('SECRETARIA')")
@RequestMapping("/turma")
@AllArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;

    @GetMapping
    public ResponseEntity<List<TurmaResponse>> findAll() {
        return ResponseEntity.ok(turmaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(turmaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TurmaResponse> save(@RequestBody @Valid TurmaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turmaService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponse> update(@PathVariable Long id, @RequestBody @Valid TurmaRequest request) {
        return ResponseEntity.ok(turmaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        turmaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}