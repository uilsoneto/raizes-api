package com.raizes;

import com.raizes.application.dto.request.MovimentacaoEstoqueRequest;
import com.raizes.application.service.EstoqueService;
import com.raizes.domain.entity.*;
import com.raizes.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EstoqueServiceTest {

    private EstoqueService service;
    private EstoqueRepository estoqueRepository;
    private ProdutoRepository produtoRepository;
    private UnidadeRepository unidadeRepository;

    private Unidade unidade;
    private Produto produto;
    private Estoque estoque;

    @BeforeEach
    void setUp() {
        estoqueRepository = mock(EstoqueRepository.class);
        produtoRepository = mock(ProdutoRepository.class);
        unidadeRepository = mock(UnidadeRepository.class);
        service = new EstoqueService(estoqueRepository, produtoRepository, unidadeRepository);

        unidade = new Unidade();
        unidade.setNome("Fortaleza");

        produto = new Produto();
        produto.setNome("Baião de Dois");
        produto.setPreco(32.90);

        estoque = new Estoque();
        estoque.setUnidade(unidade);
        estoque.setProduto(produto);
        estoque.setQuantidade(20);

        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(estoqueRepository.findByUnidadeIdAndProdutoId(any(), any())).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void entradaAumentaQuantidade() {
        var resp = service.movimentar(1L, new MovimentacaoEstoqueRequest(1L, 10, "ENTRADA"));
        assertEquals(30, resp.quantidade());
    }

    @Test
    void saidaReduzQuantidade() {
        var resp = service.movimentar(1L, new MovimentacaoEstoqueRequest(1L, 5, "SAIDA"));
        assertEquals(15, resp.quantidade());
    }

    @Test
    void saidaInsuficienteLancaConflict() {
        assertThrows(ResponseStatusException.class,
                () -> service.movimentar(1L, new MovimentacaoEstoqueRequest(1L, 100, "SAIDA")));
    }

    @Test
    void unidadeNaoEncontradaLancaNotFound() {
        when(unidadeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
                () -> service.movimentar(99L, new MovimentacaoEstoqueRequest(1L, 5, "ENTRADA")));
    }

    @Test
    void produtoNaoEncontradoLancaNotFound() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
                () -> service.movimentar(1L, new MovimentacaoEstoqueRequest(99L, 5, "ENTRADA")));
    }

    @Test
    void criaNovoEstoqueSeNaoExistir() {
        when(estoqueRepository.findByUnidadeIdAndProdutoId(any(), any())).thenReturn(Optional.empty());
        var resp = service.movimentar(1L, new MovimentacaoEstoqueRequest(1L, 5, "ENTRADA"));
        assertEquals(5, resp.quantidade());
    }
}
