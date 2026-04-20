package com.raizes.api.controller;

import com.raizes.application.dto.request.UnidadeRequest;
import com.raizes.application.dto.response.UnidadeResponse;
import com.raizes.application.service.UnidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades")
@Tag(name = "Unidades")
@SecurityRequirement(name = "bearerAuth")
public class UnidadeController {

    private final UnidadeService service;

    public UnidadeController(UnidadeService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas as unidades")
    public ResponseEntity<List<UnidadeResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar unidade por ID")
    public ResponseEntity<UnidadeResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @Operation(summary = "Criar unidade")
    public ResponseEntity<UnidadeResponse> criar(@Valid @RequestBody UnidadeRequest req) {
        return ResponseEntity.status(201).body(service.criar(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @Operation(summary = "Atualizar unidade")
    public ResponseEntity<UnidadeResponse> atualizar(@PathVariable Long id, @Valid @RequestBody UnidadeRequest req) {
        return ResponseEntity.ok(service.atualizar(id, req));
    }
}
