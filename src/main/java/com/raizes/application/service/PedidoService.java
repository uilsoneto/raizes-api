package com.raizes.application.service;

import com.raizes.application.dto.request.PedidoRequest;
import com.raizes.application.dto.request.AtualizarStatusRequest;
import com.raizes.application.dto.response.PedidoResponse;
import com.raizes.domain.entity.*;
import com.raizes.domain.enums.CanalPedido;
import com.raizes.domain.enums.StatusPedido;
import com.raizes.infrastructure.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final AuditService auditService;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository,
                         UnidadeRepository unidadeRepository, ProdutoRepository produtoRepository,
                         EstoqueRepository estoqueRepository, AuditService auditService) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.unidadeRepository = unidadeRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.auditService = auditService;
    }

    @Transactional
    public PedidoResponse criar(String emailUsuario, PedidoRequest req) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        Unidade unidade = unidadeRepository.findById(req.unidadeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setUnidade(unidade);
        pedido.setCanalPedido(req.canalPedido());

        List<ItemPedido> itens = new ArrayList<>();
        double total = 0.0;

        for (PedidoRequest.ItemPedidoRequest itemReq : req.itens()) {
            Produto produto = produtoRepository.findById(itemReq.produtoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Produto não encontrado: " + itemReq.produtoId()));

            Estoque estoque = estoqueRepository.findByUnidadeIdAndProdutoId(unidade.getId(), produto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                            "Produto sem estoque nesta unidade: " + produto.getNome()));

            if (estoque.getQuantidade() < itemReq.quantidade()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Estoque insuficiente para: " + produto.getNome() + ". Disponível: " + estoque.getQuantidade());
            }

            estoque.setQuantidade(estoque.getQuantidade() - itemReq.quantidade());
            estoqueRepository.save(estoque);

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemReq.quantidade());
            item.setPrecoUnitario(produto.getPreco());
            itens.add(item);
            total += produto.getPreco() * itemReq.quantidade();
        }

        pedido.setItens(itens);
        pedido.setTotal(total);
        Pedido salvo = pedidoRepository.save(pedido);

        auditService.registrar(usuario.getId(), "CRIAR_PEDIDO", "/pedidos",
                null, "Pedido #" + salvo.getId() + " criado via " + req.canalPedido());

        return PedidoResponse.from(salvo);
    }

    public PedidoResponse buscar(Long id) {
        return PedidoResponse.from(getOrThrow(id));
    }

    public Page<PedidoResponse> listar(CanalPedido canal, StatusPedido status, Pageable pageable) {
        if (canal != null && status != null)
            return pedidoRepository.findByCanalPedidoAndStatus(canal, status, pageable).map(PedidoResponse::from);
        if (canal != null)
            return pedidoRepository.findByCanalPedido(canal, pageable).map(PedidoResponse::from);
        if (status != null)
            return pedidoRepository.findByStatus(status, pageable).map(PedidoResponse::from);
        return pedidoRepository.findAll(pageable).map(PedidoResponse::from);
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long id, AtualizarStatusRequest req, String emailUsuario) {
        Pedido pedido = getOrThrow(id);
        validarTransicao(pedido.getStatus(), req.status());
        pedido.setStatus(req.status());
        Pedido salvo = pedidoRepository.save(pedido);

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        auditService.registrar(usuario != null ? usuario.getId() : null, "ATUALIZAR_STATUS",
                "/pedidos/" + id, null, "Status: " + req.status());

        return PedidoResponse.from(salvo);
    }

    @Transactional
    public PedidoResponse cancelar(Long id, String emailUsuario) {
        Pedido pedido = getOrThrow(id);
        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pedido já entregue não pode ser cancelado");
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        Pedido salvo = pedidoRepository.save(pedido);

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        auditService.registrar(usuario != null ? usuario.getId() : null, "CANCELAR_PEDIDO",
                "/pedidos/" + id, null, "Pedido cancelado");

        return PedidoResponse.from(salvo);
    }

    private void validarTransicao(StatusPedido atual, StatusPedido novo) {
        boolean valido = switch (atual) {
            case AGUARDANDO_PAGAMENTO -> novo == StatusPedido.PAGO || novo == StatusPedido.CANCELADO;
            case PAGO -> novo == StatusPedido.EM_PREPARO || novo == StatusPedido.CANCELADO;
            case EM_PREPARO -> novo == StatusPedido.PRONTO;
            case PRONTO -> novo == StatusPedido.ENTREGUE;
            default -> false;
        };
        if (!valido) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Transição inválida: " + atual + " → " + novo);
        }
    }

    private Pedido getOrThrow(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }
}
