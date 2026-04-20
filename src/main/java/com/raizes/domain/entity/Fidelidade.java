package com.raizes.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fidelidade")
public class Fidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @Column(nullable = false)
    private Integer pontos = 0;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Integer getPontos() { return pontos; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setPontos(Integer pontos) { this.pontos = pontos; this.atualizadoEm = LocalDateTime.now(); }
}
