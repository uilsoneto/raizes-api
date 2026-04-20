package com.raizes.infrastructure.repository;

import com.raizes.domain.entity.Fidelidade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FidelidadeRepository extends JpaRepository<Fidelidade, Long> {
    Optional<Fidelidade> findByUsuarioId(Long usuarioId);
}
