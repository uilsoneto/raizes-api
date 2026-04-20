package com.raizes.application.service;

import com.raizes.application.dto.request.UnidadeRequest;
import com.raizes.application.dto.response.UnidadeResponse;
import com.raizes.domain.entity.Unidade;
import com.raizes.infrastructure.repository.UnidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UnidadeService {

    private final UnidadeRepository repository;

    public UnidadeService(UnidadeRepository repository) {
        this.repository = repository;
    }

    public List<UnidadeResponse> listar() {
        return repository.findAll().stream().map(UnidadeResponse::from).toList();
    }

    public UnidadeResponse buscar(Long id) {
        return UnidadeResponse.from(getOrThrow(id));
    }

    @Transactional
    public UnidadeResponse criar(UnidadeRequest req) {
        Unidade u = new Unidade();
        u.setNome(req.nome());
        u.setCidade(req.cidade());
        u.setEstado(req.estado());
        return UnidadeResponse.from(repository.save(u));
    }

    @Transactional
    public UnidadeResponse atualizar(Long id, UnidadeRequest req) {
        Unidade u = getOrThrow(id);
        u.setNome(req.nome());
        u.setCidade(req.cidade());
        u.setEstado(req.estado());
        return UnidadeResponse.from(repository.save(u));
    }

    private Unidade getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));
    }
}
