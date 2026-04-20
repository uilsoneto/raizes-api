package com.raizes.api.controller;

import com.raizes.application.dto.request.LoginRequest;
import com.raizes.application.dto.request.RegisterRequest;
import com.raizes.application.dto.response.LoginResponse;
import com.raizes.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Autenticação e registro")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo cliente")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(201).body(service.register(req));
    }

    @PostMapping("/login")
    @Operation(summary = "Login e obtenção de token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(service.login(req));
    }
}
