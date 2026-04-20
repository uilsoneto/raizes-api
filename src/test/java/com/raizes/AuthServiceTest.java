package com.raizes;

import com.raizes.application.dto.request.LoginRequest;
import com.raizes.application.dto.request.RegisterRequest;
import com.raizes.application.dto.response.LoginResponse;
import com.raizes.application.service.AuthService;
import com.raizes.domain.entity.Fidelidade;
import com.raizes.domain.entity.Usuario;
import com.raizes.domain.enums.Role;
import com.raizes.infrastructure.repository.FidelidadeRepository;
import com.raizes.infrastructure.repository.UsuarioRepository;
import com.raizes.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService service;
    private UsuarioRepository usuarioRepository;
    private FidelidadeRepository fidelidadeRepository;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        fidelidadeRepository = mock(FidelidadeRepository.class);
        jwtUtil = mock(JwtUtil.class);
        service = new AuthService(usuarioRepository, fidelidadeRepository,
                new BCryptPasswordEncoder(), jwtUtil);
        when(jwtUtil.generate(any(), any())).thenReturn("token-mock");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(fidelidadeRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void registerCriaUsuarioERetornaToken() {
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        var req = new RegisterRequest("João", "joao@email.com", "senha123", true);

        LoginResponse resp = service.register(req);

        assertEquals("token-mock", resp.accessToken());
        assertEquals("CLIENTE", resp.user().perfil());
        verify(fidelidadeRepository).save(any(Fidelidade.class));
    }

    @Test
    void registerLancaConflictSeEmailJaExiste() {
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(true);
        var req = new RegisterRequest("João", "joao@email.com", "senha123", true);

        assertThrows(ResponseStatusException.class, () -> service.register(req));
    }

    @Test
    void loginRetornaTokenComCredenciaisValidas() {
        Usuario u = new Usuario();
        u.setEmail("joao@email.com");
        u.setSenha(new BCryptPasswordEncoder().encode("senha123"));
        u.setRole(Role.CLIENTE);
        u.setAtivo(true);

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(u));

        LoginResponse resp = service.login(new LoginRequest("joao@email.com", "senha123"));

        assertEquals("token-mock", resp.accessToken());
    }

    @Test
    void loginLancaUnauthorizedComSenhaErrada() {
        Usuario u = new Usuario();
        u.setEmail("joao@email.com");
        u.setSenha(new BCryptPasswordEncoder().encode("correta"));
        u.setRole(Role.CLIENTE);
        u.setAtivo(true);

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(u));

        assertThrows(ResponseStatusException.class,
                () -> service.login(new LoginRequest("joao@email.com", "errada")));
    }

    @Test
    void loginLancaUnauthorizedSeUsuarioNaoExiste() {
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> service.login(new LoginRequest("x@x.com", "123456")));
    }
}
