package com.raizes.infrastructure.repository;

import com.raizes.domain.entity.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    Optional<Estoque> findByUnidadeIdAndProdutoId(Long unidadeId, Long produtoId);
    List<Estoque> findByUnidadeId(Long unidadeId);
}
