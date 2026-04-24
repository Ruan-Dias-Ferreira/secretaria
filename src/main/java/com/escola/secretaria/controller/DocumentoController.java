package com.escola.secretaria.controller;

import com.escola.secretaria.dto.request.DocumentoRequest;
import com.escola.secretaria.dto.response.DocumentoResponse;
import com.escola.secretaria.service.DocumentoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('SECRETARIA')")
@RequestMapping("/documento")
@AllArgsConstructor
public class DocumentoController {
    private final DocumentoService documentoService;
    @GetMapping
    public ResponseEntity<List<DocumentoResponse>> findAll(){
        return  ResponseEntity.ok(documentoService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponse>findById(@PathVariable Long id){
        return ResponseEntity.ok(documentoService.findById(id));
    }
    @PostMapping
    public ResponseEntity<DocumentoResponse> save(@RequestBody @Valid DocumentoRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoService.save(request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        documentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
