-- Fake users (senha: teste123 → BCrypt)
INSERT INTO usuario (nome, email, senha, role, consentimento_lgpd, data_consentimento) VALUES
    ('Carlos Gerente',    'gerente@raizes.com',   '$2a$10$7EqJtq98hPqEX7fNZaFWoOa3uT1MkDAMFnFQFqMkDAMFnFQFqMkDA', 'GERENTE',   1, CURRENT_TIMESTAMP),
    ('Ana Atendente',     'atendente@raizes.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOa3uT1MkDAMFnFQFqMkDAMFnFQFqMkDA', 'ATENDENTE', 1, CURRENT_TIMESTAMP),
    ('João Cozinha',      'cozinha@raizes.com',   '$2a$10$7EqJtq98hPqEX7fNZaFWoOa3uT1MkDAMFnFQFqMkDAMFnFQFqMkDA', 'COZINHA',   1, CURRENT_TIMESTAMP),
    ('Maria Cliente',     'maria@email.com',      '$2a$10$7EqJtq98hPqEX7fNZaFWoOa3uT1MkDAMFnFQFqMkDAMFnFQFqMkDA', 'CLIENTE',   1, CURRENT_TIMESTAMP),
    ('Pedro Cliente',     'pedro@email.com',      '$2a$10$7EqJtq98hPqEX7fNZaFWoOa3uT1MkDAMFnFQFqMkDAMFnFQFqMkDA', 'CLIENTE',   1, CURRENT_TIMESTAMP),
    ('Lucia Cliente',     'lucia@email.com',      '$2a$10$7EqJtq98hPqEX7fNZaFWoOa3uT1MkDAMFnFQFqMkDAMFnFQFqMkDA', 'CLIENTE',   0, NULL);
-- senha: teste123

-- Fidelidade para novos usuários
INSERT INTO fidelidade (usuario_id, pontos) VALUES
    (2, 0), (3, 0), (4, 0),
    (5, 320),
    (6, 75),
    (7, 0);

-- Pedidos com status variados
-- Maria (id=5): pedido ENTREGUE via APP
INSERT INTO pedido (usuario_id, unidade_id, canal_pedido, status, total, criado_em) VALUES
    (5, 1, 'APP',   'ENTREGUE',            51.80, datetime('now', '-2 days')),
    (5, 1, 'APP',   'PAGO',                32.90, datetime('now', '-1 day')),
    (6, 2, 'TOTEM', 'EM_PREPARO',          64.80, datetime('now', '-3 hours')),
    (6, 2, 'TOTEM', 'PRONTO',              18.90, datetime('now', '-1 hour')),
    (7, 3, 'BALCAO','AGUARDANDO_PAGAMENTO', 9.90, datetime('now', '-10 minutes')),
    (7, 1, 'APP',   'CANCELADO',           45.90, datetime('now', '-5 days'));

-- Itens dos pedidos
INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES
    (1, 3, 1, 18.90), (1, 1, 1, 32.90),  -- pedido 1: tapioca + baião
    (2, 1, 1, 32.90),                     -- pedido 2: baião
    (3, 2, 1, 45.90), (3, 4, 2, 9.90),   -- pedido 3: carne de sol + 2 sucos
    (4, 3, 1, 18.90),                     -- pedido 4: tapioca
    (5, 4, 1,  9.90),                     -- pedido 5: suco (aguardando pagamento)
    (6, 2, 1, 45.90);                     -- pedido 6: cancelado

-- Pagamentos
INSERT INTO pagamento (pedido_id, forma_pagamento, status, valor, payload_retorno, processado_em) VALUES
    (1, 'PIX',        'APROVADO', 51.80, '{"gateway":"MOCK","resultado":"APROVADO"}', datetime('now', '-2 days')),
    (2, 'CARTAO',     'APROVADO', 32.90, '{"gateway":"MOCK","resultado":"APROVADO"}', datetime('now', '-1 day')),
    (3, 'PIX',        'APROVADO', 64.80, '{"gateway":"MOCK","resultado":"APROVADO"}', datetime('now', '-3 hours')),
    (4, 'DINHEIRO',   'APROVADO', 18.90, '{"gateway":"MOCK","resultado":"APROVADO"}', datetime('now', '-1 hour')),
    (6, 'CARTAO',     'RECUSADO', 45.90, '{"gateway":"MOCK","resultado":"RECUSADO"}', datetime('now', '-5 days'));

-- Atualiza pontos de fidelidade (Maria: 320 + pedidos entregues)
UPDATE fidelidade SET pontos = 370 WHERE usuario_id = 5;

-- Audit log de exemplo
INSERT INTO audit_log (usuario_id, acao, recurso, ip_origem, detalhes) VALUES
    (5, 'CRIAR_PEDIDO',    'pedido/1', '192.168.1.10', 'Canal: APP'),
    (5, 'PAGAMENTO',       'pedido/1', '192.168.1.10', 'Forma: PIX | Status: APROVADO'),
    (5, 'CRIAR_PEDIDO',    'pedido/2', '192.168.1.10', 'Canal: APP'),
    (6, 'CRIAR_PEDIDO',    'pedido/3', '10.0.0.5',     'Canal: TOTEM'),
    (6, 'PAGAMENTO',       'pedido/3', '10.0.0.5',     'Forma: PIX | Status: APROVADO'),
    (7, 'CANCELAR_PEDIDO', 'pedido/6', '172.16.0.3',   'Motivo: solicitado pelo cliente');
