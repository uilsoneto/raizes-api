package com.raizes.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private Double preco;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private boolean disponivel = true;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public Double getPreco() { return preco; }
    public String getCategoria() { return categoria; }
    public boolean isDisponivel() { return disponivel; }

    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPreco(Double preco) { this.preco = preco; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}
