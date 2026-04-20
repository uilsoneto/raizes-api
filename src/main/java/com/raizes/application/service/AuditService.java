package com.raizes.application.service;

import com.raizes.domain.entity.AuditLog;
import com.raizes.infrastructure.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Async
    public void registrar(Long usuarioId, String acao, String recurso, String ip, String detalhes) {
        AuditLog log = new AuditLog();
        log.setUsuarioId(usuarioId);
        log.setAcao(acao);
        log.setRecurso(recurso);
        log.setIpOrigem(ip);
        log.setDetalhes(detalhes);
        repository.save(log);
    }
}
