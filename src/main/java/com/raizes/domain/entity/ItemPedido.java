package com.raizes.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false)
    private Double precoUnitario;

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public Produto getProduto() { return produto; }
    public Integer getQuantidade() { return quantidade; }
    public Double getPrecoUnitario() { return precoUnitario; }

    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public void setPrecoUnitario(Double precoUnitario) { this.precoUnitario = precoUnitario; }
}
