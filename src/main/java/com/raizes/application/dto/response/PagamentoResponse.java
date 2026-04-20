package com.raizes.application.dto.response;

import com.raizes.domain.entity.Pagamento;
import com.raizes.domain.enums.FormaPagamento;
import com.raizes.domain.enums.StatusPagamento;
import java.time.LocalDateTime;

public record PagamentoResponse(
        Long id,
        Long pedidoId,
        FormaPagamento formaPagamento,
        StatusPagamento status,
        Double valor,
        String payloadRetorno,
        LocalDateTime processadoEm
) {
    public static PagamentoResponse from(Pagamento p) {
        return new PagamentoResponse(p.getId(), p.getPedido().getId(), p.getFormaPagamento(),
                p.getStatus(), p.getValor(), p.getPayloadRetorno(), p.getProcessadoEm());
    }
}
