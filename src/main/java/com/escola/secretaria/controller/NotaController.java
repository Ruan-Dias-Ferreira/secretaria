package com.escola.secretaria.controller;

import com.escola.secretaria.dto.request.NotaRequest;
import com.escola.secretaria.dto.response.NotaResponse;
import com.escola.secretaria.service.NotaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('SECRETARIA','PROFESSOR')")
@RequestMapping("/nota")
@AllArgsConstructor
public class NotaController {

    private final NotaService notaService;

    @GetMapping
    public ResponseEntity<List<NotaResponse>> findAll() {
        return ResponseEntity.ok(notaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<NotaResponse> save(@RequestBody @Valid NotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notaService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotaResponse> update(@PathVariable Long id, @RequestBody @Valid NotaRequest request) {
        return ResponseEntity.ok(notaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}