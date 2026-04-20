package com.raizes.application.service;

import com.raizes.application.dto.request.ResgateRequest;
import com.raizes.application.dto.response.FidelidadeResponse;
import com.raizes.domain.entity.Fidelidade;
import com.raizes.infrastructure.repository.FidelidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FidelidadeService {

    private final FidelidadeRepository repository;

    public FidelidadeService(FidelidadeRepository repository) {
        this.repository = repository;
    }

    public FidelidadeResponse consultar(Long usuarioId) {
        return FidelidadeResponse.from(getOrThrow(usuarioId));
    }

    @Transactional
    public FidelidadeResponse resgatar(Long usuarioId, ResgateRequest req) {
        Fidelidade f = getOrThrow(usuarioId);
        if (f.getPontos() < req.pontos()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Pontos insuficientes. Disponível: " + f.getPontos());
        }
        f.setPontos(f.getPontos() - req.pontos());
        return FidelidadeResponse.from(repository.save(f));
    }

    private Fidelidade getOrThrow(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fidelidade não encontrada"));
    }
}
