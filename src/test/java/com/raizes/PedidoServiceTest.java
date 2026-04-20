package com.raizes;

import com.raizes.application.dto.request.PedidoRequest;
import com.raizes.application.dto.request.AtualizarStatusRequest;
import com.raizes.application.service.AuditService;
import com.raizes.application.service.PedidoService;
import com.raizes.domain.entity.*;
import com.raizes.domain.enums.*;
import com.raizes.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    private PedidoService service;
    private PedidoRepository pedidoRepository;
    private UsuarioRepository usuarioRepository;
    private UnidadeRepository unidadeRepository;
    private ProdutoRepository produtoRepository;
    private EstoqueRepository estoqueRepository;

    private Usuario usuario;
    private Unidade unidade;
    private Produto produto;
    private Estoque estoque;

    @BeforeEach
    void setUp() {
        pedidoRepository = mock(PedidoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        unidadeRepository = mock(UnidadeRepository.class);
        produtoRepository = mock(ProdutoRepository.class);
        estoqueRepository = mock(EstoqueRepository.class);
        AuditService auditService = mock(AuditService.class);

        service = new PedidoService(pedidoRepository, usuarioRepository, unidadeRepository,
                produtoRepository, estoqueRepository, auditService);

        usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
        usuario.setRole(Role.CLIENTE);

        unidade = new Unidade();
        unidade.setNome("Fortaleza");
        unidade.setCidade("Fortaleza");
        unidade.setEstado("CE");

        produto = new Produto();
        produto.setNome("Baião de Dois");
        produto.setPreco(32.90);

        estoque = new Estoque();
        estoque.setUnidade(unidade);
        estoque.setProduto(produto);
        estoque.setQuantidade(10);

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(estoqueRepository.findByUnidadeIdAndProdutoId(any(), any())).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pedidoRepository.save(any())).thenAnswer(i -> {
            Pedido p = i.getArgument(0);
            return p;
        });
    }

    @Test
    void criarPedidoComSucesso() {
        var req = new PedidoRequest(1L, CanalPedido.APP, FormaPagamento.MOCK,
                List.of(new PedidoRequest.ItemPedidoRequest(1L, 2)));

        var resp = service.criar("joao@email.com", req);

        assertEquals(CanalPedido.APP, resp.canalPedido());
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, resp.status());
        assertEquals(65.80, resp.total(), 0.01);
    }

    @Test
    void criarPedidoRegistraCanalPedido() {
        var req = new PedidoRequest(1L, CanalPedido.TOTEM, FormaPagamento.MOCK,
                List.of(new PedidoRequest.ItemPedidoRequest(1L, 1)));

        var resp = service.criar("joao@email.com", req);

        assertEquals(CanalPedido.TOTEM, resp.canalPedido());
    }

    @Test
    void criarPedidoLancaConflictComEstoqueInsuficiente() {
        estoque.setQuantidade(1);
        var req = new PedidoRequest(1L, CanalPedido.WEB, FormaPagamento.MOCK,
                List.of(new PedidoRequest.ItemPedidoRequest(1L, 5)));

        assertThrows(ResponseStatusException.class, () -> service.criar("joao@email.com", req));
    }

    @Test
    void criarPedidoLancaNotFoundSeProdutoNaoExiste() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());
        var req = new PedidoRequest(1L, CanalPedido.APP, FormaPagamento.MOCK,
                List.of(new PedidoRequest.ItemPedidoRequest(99L, 1)));

        assertThrows(ResponseStatusException.class, () -> service.criar("joao@email.com", req));
    }

    @Test
    void criarPedidoLancaNotFoundSeUnidadeNaoExiste() {
        when(unidadeRepository.findById(99L)).thenReturn(Optional.empty());
        var req = new PedidoRequest(99L, CanalPedido.APP, FormaPagamento.MOCK,
                List.of(new PedidoRequest.ItemPedidoRequest(1L, 1)));

        assertThrows(ResponseStatusException.class, () -> service.criar("joao@email.com", req));
    }

    @Test
    void atualizarStatusTransicaoValidaAguardandoParaPago() {
        Pedido pedido = pedidoComStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var resp = service.atualizarStatus(1L, new AtualizarStatusRequest(StatusPedido.PAGO), "joao@email.com");

        assertEquals(StatusPedido.PAGO, resp.status());
    }

    @Test
    void atualizarStatusTransicaoInvalidaLancaConflict() {
        Pedido pedido = pedidoComStatus(StatusPedido.ENTREGUE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(ResponseStatusException.class,
                () -> service.atualizarStatus(1L, new AtualizarStatusRequest(StatusPedido.PAGO), "joao@email.com"));
    }

    @Test
    void cancelarPedidoComSucesso() {
        Pedido pedido = pedidoComStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var resp = service.cancelar(1L, "joao@email.com");

        assertEquals(StatusPedido.CANCELADO, resp.status());
    }

    @Test
    void cancelarPedidoEntregueDeveLancarConflict() {
        Pedido pedido = pedidoComStatus(StatusPedido.ENTREGUE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(ResponseStatusException.class, () -> service.cancelar(1L, "joao@email.com"));
    }

    private Pedido pedidoComStatus(StatusPedido status) {
        Pedido p = new Pedido();
        p.setUsuario(usuario);
        p.setUnidade(unidade);
        p.setCanalPedido(CanalPedido.APP);
        p.setStatus(status);
        p.setTotal(32.90);
        p.setItens(List.of());
        return p;
    }
}
