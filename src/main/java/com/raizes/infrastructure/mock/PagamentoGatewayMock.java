package com.raizes.infrastructure.mock;

import com.raizes.domain.enums.StatusPagamento;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Simula um gateway de pagamento externo.
 * Retorna RECUSADO se o valor for múltiplo de 13 (cenário de teste de falha).
 */
@Component
public class PagamentoGatewayMock {

    public record ResultadoPagamento(StatusPagamento status, String payload) {}

    public ResultadoPagamento processar(Double valor) {
        boolean aprovado = (valor % 13 != 0);
        String transacaoId = UUID.randomUUID().toString();
        String status = aprovado ? "APROVADO" : "RECUSADO";
        String payload = """
                {"transacaoId":"%s","status":"%s","valor":%.2f,"gateway":"MOCK_GATEWAY"}
                """.formatted(transacaoId, status, valor).strip();
        return new ResultadoPagamento(
                aprovado ? StatusPagamento.APROVADO : StatusPagamento.RECUSADO,
                payload);
    }
}
