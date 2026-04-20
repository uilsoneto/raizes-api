package com.raizes.domain.entity;

import com.raizes.domain.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "consentimento_lgpd", nullable = false)
    private boolean consentimentoLgpd = false;

    @Column(name = "data_consentimento")
    private LocalDateTime dataConsentimento;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(nullable = false)
    private boolean ativo = true;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public Role getRole() { return role; }
    public boolean isConsentimentoLgpd() { return consentimentoLgpd; }
    public LocalDateTime getDataConsentimento() { return dataConsentimento; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public boolean isAtivo() { return ativo; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setRole(Role role) { this.role = role; }
    public void setConsentimentoLgpd(boolean consentimentoLgpd) { this.consentimentoLgpd = consentimentoLgpd; }
    public void setDataConsentimento(LocalDateTime dataConsentimento) { this.dataConsentimento = dataConsentimento; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
