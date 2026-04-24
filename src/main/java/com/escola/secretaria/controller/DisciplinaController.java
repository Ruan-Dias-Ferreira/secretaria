package com.escola.secretaria.controller;

import com.escola.secretaria.dto.request.DisciplinaRequest;
import com.escola.secretaria.dto.response.DisciplinaResponse;
import com.escola.secretaria.service.DisciplinaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/disciplina")
@AllArgsConstructor
public class DisciplinaController {
    private final DisciplinaService disciplinaService;

    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<DisciplinaResponse>> findAll(){
        return ResponseEntity.ok(disciplinaService.findAll());
    }
    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @GetMapping("/{id}")
    public ResponseEntity<DisciplinaResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(disciplinaService.findById(id));
    }
    @PreAuthorize("hasAnyRole('SECRETARIA')")
    @PostMapping
    public ResponseEntity<DisciplinaResponse> save(@RequestBody @Valid DisciplinaRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(disciplinaService.save(request));
    }
    @PreAuthorize("hasAnyRole('SECRETARIA')")
    @PutMapping("/{id}")
    public ResponseEntity<DisciplinaResponse> update(@PathVariable Long id, @RequestBody @Valid DisciplinaRequest request){
        return ResponseEntity.ok(disciplinaService.update(id, request));
    }
    @PreAuthorize("hasAnyRole('SECRETARIA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        disciplinaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
