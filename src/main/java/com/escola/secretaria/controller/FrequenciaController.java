package com.escola.secretaria.controller;

import com.escola.secretaria.dto.request.FrequenciaRequest;
import com.escola.secretaria.dto.response.FrequenciaResponse;
import com.escola.secretaria.service.FrequenciaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/frequencia")
@AllArgsConstructor
public class FrequenciaController {
    private final FrequenciaService frequenciaService;
    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<FrequenciaResponse>> findAll(){
        return ResponseEntity.ok(frequenciaService.findAll());
    }
    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @GetMapping("/{id}")
    public ResponseEntity<FrequenciaResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(frequenciaService.findById(id));
    }
    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @PostMapping
    public ResponseEntity<FrequenciaResponse> save(@RequestBody @Valid FrequenciaRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(frequenciaService.save(request));
    }
    @PreAuthorize("hasAnyRole('SECRETARIA', 'PROFESSOR')")
    @PutMapping("/{id}")
    public ResponseEntity<FrequenciaResponse> update(@PathVariable Long id, @RequestBody @Valid FrequenciaRequest request){
        return ResponseEntity.ok(frequenciaService.update(id, request));
    }
    @PreAuthorize("hasRole('SECRETARIA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        frequenciaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
