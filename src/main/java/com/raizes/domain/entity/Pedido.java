package com.raizes.domain.entity;

import com.raizes.domain.enums.CanalPedido;
import com.raizes.domain.enums.StatusPedido;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_pedido", nullable = false)
    private CanalPedido canalPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;

    @Column(nullable = false)
    private Double total = 0.0;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Unidade getUnidade() { return unidade; }
    public CanalPedido getCanalPedido() { return canalPedido; }
    public StatusPedido getStatus() { return status; }
    public Double getTotal() { return total; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public List<ItemPedido> getItens() { return itens; }

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setUnidade(Unidade unidade) { this.unidade = unidade; }
    public void setCanalPedido(CanalPedido canalPedido) { this.canalPedido = canalPedido; }
    public void setStatus(StatusPedido status) { this.status = status; this.atualizadoEm = LocalDateTime.now(); }
    public void setTotal(Double total) { this.total = total; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
}
