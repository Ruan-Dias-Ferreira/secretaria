package com.escola.secretaria.controller;

import com.escola.secretaria.dto.request.FrequenciaRequest;
import com.escola.secretaria.dto.response.FrequenciaResponse;
import com.escola.secretaria.service.FrequenciaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/frequencia")
@AllArgsConstructor
public class FrequenciaController {
    private final FrequenciaService frequenciaService;

    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<FrequenciaResponse>> findAll() {
        return ResponseEntity.ok(frequenciaService.findAll());
    }

    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<FrequenciaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(frequenciaService.findById(id));
    }

    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @GetMapping("/por-data-turma")
    public ResponseEntity<List<FrequenciaResponse>> findByTurmaAndData(
            @RequestParam Long turmaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(frequenciaService.findByTurmaAndData(turmaId, data));
    }

    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @GetMapping("/resumo-dia")
    public ResponseEntity<FrequenciaService.ResumoDia> resumoDia(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(frequenciaService.resumoDia(data != null ? data : LocalDate.now()));
    }

    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @GetMapping("/percentual")
    public ResponseEntity<Map<String, Object>> percentual(
            @RequestParam Long alunoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        double pct = frequenciaService.percentualPresenca(alunoId, inicio, fim);
        return ResponseEntity.ok(Map.of(
                "alunoId", alunoId,
                "inicio", inicio,
                "fim", fim,
                "percentual", pct
        ));
    }

    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @PostMapping
    public ResponseEntity<FrequenciaResponse> save(@RequestBody @Valid FrequenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(frequenciaService.save(request));
    }

    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @PostMapping("/lote")
    public ResponseEntity<List<FrequenciaResponse>> saveLote(@RequestBody @Valid List<FrequenciaRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(frequenciaService.saveLote(requests));
    }

    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<FrequenciaResponse> update(@PathVariable Long id, @RequestBody @Valid FrequenciaRequest request) {
        return ResponseEntity.ok(frequenciaService.update(id, request));
    }

    @PreAuthorize("hasRole('SECRETARIA')")
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        frequenciaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
