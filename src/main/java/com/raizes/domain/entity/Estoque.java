package com.raizes.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "estoque", uniqueConstraints = @UniqueConstraint(columnNames = {"unidade_id", "produto_id"}))
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade = 0;

    public Long getId() { return id; }
    public Unidade getUnidade() { return unidade; }
    public Produto getProduto() { return produto; }
    public Integer getQuantidade() { return quantidade; }

    public void setUnidade(Unidade unidade) { this.unidade = unidade; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
