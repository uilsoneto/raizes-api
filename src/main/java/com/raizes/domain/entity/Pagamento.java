package com.raizes.domain.entity;

import com.raizes.domain.enums.FormaPagamento;
import com.raizes.domain.enums.StatusPagamento;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(nullable = false)
    private Double valor;

    @Column(name = "payload_retorno", columnDefinition = "TEXT")
    private String payloadRetorno;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "processado_em")
    private LocalDateTime processadoEm;

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public StatusPagamento getStatus() { return status; }
    public Double getValor() { return valor; }
    public String getPayloadRetorno() { return payloadRetorno; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getProcessadoEm() { return processadoEm; }

    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }
    public void setStatus(StatusPagamento status) { this.status = status; }
    public void setValor(Double valor) { this.valor = valor; }
    public void setPayloadRetorno(String payloadRetorno) { this.payloadRetorno = payloadRetorno; }
    public void setProcessadoEm(LocalDateTime processadoEm) { this.processadoEm = processadoEm; }
}
