package com.raizes.application.service;

import com.raizes.application.dto.request.LoginRequest;
import com.raizes.application.dto.request.RegisterRequest;
import com.raizes.application.dto.response.LoginResponse;
import com.raizes.domain.entity.Fidelidade;
import com.raizes.domain.entity.Usuario;
import com.raizes.domain.enums.Role;
import com.raizes.infrastructure.repository.FidelidadeRepository;
import com.raizes.infrastructure.repository.UsuarioRepository;
import com.raizes.infrastructure.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final FidelidadeRepository fidelidadeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, FidelidadeRepository fidelidadeRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.fidelidadeRepository = fidelidadeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public LoginResponse register(RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        Usuario u = new Usuario();
        u.setNome(req.nome());
        u.setEmail(req.email());
        u.setSenha(passwordEncoder.encode(req.senha()));
        u.setRole(Role.CLIENTE);
        u.setConsentimentoLgpd(req.consentimentoLgpd());
        if (req.consentimentoLgpd()) u.setDataConsentimento(LocalDateTime.now());
        usuarioRepository.save(u);

        Fidelidade f = new Fidelidade();
        f.setUsuario(u);
        fidelidadeRepository.save(f);

        String token = jwtUtil.generate(u.getEmail(), u.getRole().name());
        return new LoginResponse(token, "Bearer", 3600,
                new LoginResponse.UsuarioResumo(u.getId(), u.getNome(), u.getRole().name()));
    }

    public LoginResponse login(LoginRequest req) {
        Usuario u = usuarioRepository.findByEmail(req.email())
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));
        if (!passwordEncoder.matches(req.senha(), u.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }
        String token = jwtUtil.generate(u.getEmail(), u.getRole().name());
        return new LoginResponse(token, "Bearer", 3600,
                new LoginResponse.UsuarioResumo(u.getId(), u.getNome(), u.getRole().name()));
    }
}
