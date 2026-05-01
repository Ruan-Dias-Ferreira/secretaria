package com.escola.secretaria.controller;

import com.escola.secretaria.dto.request.EventoRequest;
import com.escola.secretaria.dto.response.EventoResponse;
import com.escola.secretaria.service.EventoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/eventos")
@AllArgsConstructor
public class EventoController {
    private final EventoService eventoService;

    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<EventoResponse>> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        if (inicio != null && fim != null) {
            return ResponseEntity.ok(eventoService.findByPeriodo(inicio, fim));
        }
        return ResponseEntity.ok(eventoService.findAll());
    }

    @PreAuthorize("hasRole('SECRETARIA')")
    @PostMapping
    public ResponseEntity<EventoService.EventoSaveResult> save(@RequestBody @Valid EventoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.save(request));
    }

    @PreAuthorize("hasRole('SECRETARIA')")
    @PutMapping("/{id}")
    public ResponseEntity<EventoService.EventoSaveResult> update(@PathVariable Long id, @RequestBody @Valid EventoRequest request) {
        return ResponseEntity.ok(eventoService.update(id, request));
    }

    @PreAuthorize("hasRole('SECRETARIA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
