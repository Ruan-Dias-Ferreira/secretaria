package com.escola.secretaria.controller;

import com.escola.secretaria.repository.TurmaRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@PreAuthorize("hasRole('SECRETARIA')")
@RequestMapping("/ano-letivo")
@AllArgsConstructor
public class AnoLetivoController {

    private final TurmaRepository turmaRepository;

    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> exists(@RequestParam int ano) {
        return ResponseEntity.ok(Map.of("exists", turmaRepository.existsByAnoLetivo(ano)));
    }
}
