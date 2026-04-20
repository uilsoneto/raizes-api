package com.raizes.application.service;

import com.raizes.application.dto.request.ProdutoRequest;
import com.raizes.application.dto.response.ProdutoResponse;
import com.raizes.domain.entity.Produto;
import com.raizes.infrastructure.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Page<ProdutoResponse> listar(Pageable pageable) {
        return repository.findByDisponivelTrue(pageable).map(ProdutoResponse::from);
    }

    public ProdutoResponse buscar(Long id) {
        return ProdutoResponse.from(getOrThrow(id));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest req) {
        Produto p = new Produto();
        p.setNome(req.nome());
        p.setDescricao(req.descricao());
        p.setPreco(req.preco());
        p.setCategoria(req.categoria());
        return ProdutoResponse.from(repository.save(p));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest req) {
        Produto p = getOrThrow(id);
        p.setNome(req.nome());
        p.setDescricao(req.descricao());
        p.setPreco(req.preco());
        p.setCategoria(req.categoria());
        return ProdutoResponse.from(repository.save(p));
    }

    @Transactional
    public void desativar(Long id) {
        Produto p = getOrThrow(id);
        p.setDisponivel(false);
        repository.save(p);
    }

    public Produto getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }
}
