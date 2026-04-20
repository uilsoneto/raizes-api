package com.raizes.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false)
    private String acao;

    @Column(nullable = false)
    private String recurso;

    @Column(name = "ip_origem")
    private String ipOrigem;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String detalhes;

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getAcao() { return acao; }
    public String getRecurso() { return recurso; }
    public String getIpOrigem() { return ipOrigem; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getDetalhes() { return detalhes; }

    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public void setAcao(String acao) { this.acao = acao; }
    public void setRecurso(String recurso) { this.recurso = recurso; }
    public void setIpOrigem(String ipOrigem) { this.ipOrigem = ipOrigem; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }
}
