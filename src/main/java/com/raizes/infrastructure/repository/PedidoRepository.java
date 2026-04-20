package com.raizes.infrastructure.repository;

import com.raizes.domain.entity.Pedido;
import com.raizes.domain.enums.CanalPedido;
import com.raizes.domain.enums.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Page<Pedido> findByUsuarioId(Long usuarioId, Pageable pageable);
    Page<Pedido> findByCanalPedido(CanalPedido canal, Pageable pageable);
    Page<Pedido> findByStatus(StatusPedido status, Pageable pageable);
    Page<Pedido> findByCanalPedidoAndStatus(CanalPedido canal, StatusPedido status, Pageable pageable);
}
