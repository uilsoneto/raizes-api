package com.raizes;

import com.raizes.application.service.PagamentoService;
import com.raizes.application.service.AuditService;
import com.raizes.domain.entity.*;
import com.raizes.domain.enums.*;
import com.raizes.infrastructure.mock.PagamentoGatewayMock;
import com.raizes.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PagamentoServiceTest {

    private PagamentoService service;
    private PagamentoRepository pagamentoRepository;
    private PedidoRepository pedidoRepository;
    private FidelidadeRepository fidelidadeRepository;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pagamentoRepository = mock(PagamentoRepository.class);
        pedidoRepository = mock(PedidoRepository.class);
        fidelidadeRepository = mock(FidelidadeRepository.class);
        AuditService auditService = mock(AuditService.class);

        service = new PagamentoService(pagamentoRepository, pedidoRepository,
                fidelidadeRepository, new PagamentoGatewayMock(), auditService);

        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
        usuario.setRole(Role.CLIENTE);

        Unidade unidade = new Unidade();
        unidade.setNome("Fortaleza");

        pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setUnidade(unidade);
        pedido.setCanalPedido(CanalPedido.APP);
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        pedido.setTotal(32.90);
        pedido.setItens(List.of());

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pagamentoRepository.findByPedidoId(1L)).thenReturn(Optional.empty());
        when(pagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pedidoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(fidelidadeRepository.findByUsuarioId(any())).thenReturn(Optional.empty());
    }

    @Test
    void pagamentoAprovadoAtualizaStatusParaPago() {
        // 32.90 não é múltiplo de 13 → aprovado
        var resp = service.processar(1L, FormaPagamento.MOCK);

        assertEquals(StatusPagamento.APROVADO, resp.status());
        assertEquals(StatusPedido.PAGO, pedido.getStatus());
    }

    @Test
    void pagamentoRecusadoMantemStatusAguardando() {
        pedido.setTotal(26.0); // 26 = 13*2 → recusado pelo mock
        var resp = service.processar(1L, FormaPagamento.MOCK);

        assertEquals(StatusPagamento.RECUSADO, resp.status());
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedido.getStatus());
    }

    @Test
    void pagamentoDuplicadoLancaConflict() {
        Pagamento existente = new Pagamento();
        when(pagamentoRepository.findByPedidoId(1L)).thenReturn(Optional.of(existente));

        assertThrows(ResponseStatusException.class, () -> service.processar(1L, FormaPagamento.MOCK));
    }

    @Test
    void pagamentoPedidoNaoAguardandoLancaConflict() {
        pedido.setStatus(StatusPedido.PAGO);

        assertThrows(ResponseStatusException.class, () -> service.processar(1L, FormaPagamento.MOCK));
    }

    @Test
    void pagamentoPedidoNaoEncontradoLancaNotFound() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.processar(99L, FormaPagamento.MOCK));
    }

    @Test
    void pagamentoAprovadoCreditaPontosDeFidelidade() {
        Fidelidade fidelidade = new Fidelidade();
        fidelidade.setPontos(0);
        fidelidade.setUsuario(pedido.getUsuario());
        when(fidelidadeRepository.findByUsuarioId(any())).thenReturn(Optional.of(fidelidade));
        when(fidelidadeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.processar(1L, FormaPagamento.MOCK);

        // 32.90 / 10 = 3 pontos
        assertEquals(3, fidelidade.getPontos());
    }
}
