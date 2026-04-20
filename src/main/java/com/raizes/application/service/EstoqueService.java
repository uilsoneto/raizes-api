package com.raizes.application.service;

import com.raizes.application.dto.request.MovimentacaoEstoqueRequest;
import com.raizes.application.dto.response.EstoqueResponse;
import com.raizes.domain.entity.Estoque;
import com.raizes.domain.entity.Produto;
import com.raizes.domain.entity.Unidade;
import com.raizes.infrastructure.repository.EstoqueRepository;
import com.raizes.infrastructure.repository.ProdutoRepository;
import com.raizes.infrastructure.repository.UnidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;

    public EstoqueService(EstoqueRepository estoqueRepository, ProdutoRepository produtoRepository,
                          UnidadeRepository unidadeRepository) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
        this.unidadeRepository = unidadeRepository;
    }

    public List<EstoqueResponse> listarPorUnidade(Long unidadeId) {
        return estoqueRepository.findByUnidadeId(unidadeId).stream().map(EstoqueResponse::from).toList();
    }

    @Transactional
    public EstoqueResponse movimentar(Long unidadeId, MovimentacaoEstoqueRequest req) {
        Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));
        Produto produto = produtoRepository.findById(req.produtoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        Estoque estoque = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, req.produtoId())
                .orElseGet(() -> {
                    Estoque novo = new Estoque();
                    novo.setUnidade(unidade);
                    novo.setProduto(produto);
                    return novo;
                });

        if ("SAIDA".equalsIgnoreCase(req.tipo())) {
            if (estoque.getQuantidade() < req.quantidade()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente");
            }
            estoque.setQuantidade(estoque.getQuantidade() - req.quantidade());
        } else {
            estoque.setQuantidade(estoque.getQuantidade() + req.quantidade());
        }
        return EstoqueResponse.from(estoqueRepository.save(estoque));
    }

    public Integer consultarQuantidade(Long unidadeId, Long produtoId) {
        return estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                .map(Estoque::getQuantidade).orElse(0);
    }
}
