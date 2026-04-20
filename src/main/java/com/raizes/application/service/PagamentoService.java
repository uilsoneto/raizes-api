package com.raizes.application.service;

import com.raizes.application.dto.response.PagamentoResponse;
import com.raizes.domain.entity.Pagamento;
import com.raizes.domain.entity.Pedido;
import com.raizes.domain.enums.FormaPagamento;
import com.raizes.domain.enums.StatusPagamento;
import com.raizes.domain.enums.StatusPedido;
import com.raizes.infrastructure.mock.PagamentoGatewayMock;
import com.raizes.infrastructure.repository.FidelidadeRepository;
import com.raizes.infrastructure.repository.PagamentoRepository;
import com.raizes.infrastructure.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final FidelidadeRepository fidelidadeRepository;
    private final PagamentoGatewayMock gateway;
    private final AuditService auditService;

    public PagamentoService(PagamentoRepository pagamentoRepository, PedidoRepository pedidoRepository,
                             FidelidadeRepository fidelidadeRepository, PagamentoGatewayMock gateway,
                             AuditService auditService) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.fidelidadeRepository = fidelidadeRepository;
        this.gateway = gateway;
        this.auditService = auditService;
    }

    @Transactional
    public PagamentoResponse processar(Long pedidoId, FormaPagamento forma) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pedido não está aguardando pagamento");
        }

        if (pagamentoRepository.findByPedidoId(pedidoId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pagamento já registrado para este pedido");
        }

        var resultado = gateway.processar(pedido.getTotal());

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setFormaPagamento(forma);
        pagamento.setValor(pedido.getTotal());
        pagamento.setStatus(resultado.status());
        pagamento.setPayloadRetorno(resultado.payload());
        pagamento.setProcessadoEm(LocalDateTime.now());
        pagamentoRepository.save(pagamento);

        if (resultado.status() == StatusPagamento.APROVADO) {
            pedido.setStatus(StatusPedido.PAGO);
            pedidoRepository.save(pedido);
            creditarPontos(pedido);
        }

        auditService.registrar(pedido.getUsuario().getId(), "PAGAMENTO",
                "/pagamentos", null, "Pedido #" + pedidoId + " — " + resultado.status());

        return PagamentoResponse.from(pagamento);
    }

    public PagamentoResponse consultar(Long pedidoId) {
        return PagamentoResponse.from(pagamentoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagamento não encontrado")));
    }

    private void creditarPontos(Pedido pedido) {
        int pontos = (int) Math.floor(pedido.getTotal() / 10);
        if (pontos <= 0) return;
        fidelidadeRepository.findByUsuarioId(pedido.getUsuario().getId()).ifPresent(f -> {
            f.setPontos(f.getPontos() + pontos);
            fidelidadeRepository.save(f);
        });
    }
}
