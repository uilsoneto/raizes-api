-- Seed: unidades (only insert if table is empty)
INSERT OR IGNORE INTO unidade (id, nome, cidade, estado, ativa) VALUES
    (1, 'Raízes Fortaleza Centro', 'Fortaleza', 'CE', 1),
    (2, 'Raízes Recife Boa Viagem', 'Recife', 'PE', 1),
    (3, 'Raízes Salvador Pelourinho', 'Salvador', 'BA', 1);

-- Seed: produtos
INSERT OR IGNORE INTO produto (id, nome, descricao, preco, categoria, disponivel) VALUES
    (1, 'Baião de Dois', 'Arroz com feijão de corda, queijo coalho e bacon', 32.90, 'Prato Principal', 1),
    (2, 'Carne de Sol na Brasa', 'Carne de sol grelhada com manteiga de garrafa', 45.90, 'Prato Principal', 1),
    (3, 'Tapioca Recheada', 'Tapioca com queijo e carne seca', 18.90, 'Lanche', 1),
    (4, 'Suco de Cajá', 'Suco natural de cajá 500ml', 9.90, 'Bebida', 1),
    (5, 'Pamonha Doce', 'Pamonha artesanal de milho verde', 8.50, 'Sobremesa', 1);

-- Seed: estoque por unidade
INSERT OR IGNORE INTO estoque (id, unidade_id, produto_id, quantidade) VALUES
    (1, 1, 1, 50), (2, 1, 2, 30), (3, 1, 3, 80), (4, 1, 4, 100), (5, 1, 5, 60),
    (6, 2, 1, 40), (7, 2, 2, 25), (8, 2, 3, 70), (9, 2, 4, 90),  (10, 2, 5, 50),
    (11, 3, 1, 35), (12, 3, 2, 20), (13, 3, 3, 60), (14, 3, 4, 80), (15, 3, 5, 45);

-- Seed: admin user (senha: admin123 - BCrypt hash)
INSERT OR IGNORE INTO usuario (id, nome, email, senha, role, consentimento_lgpd, data_consentimento, criado_em, ativo) VALUES
    (1, 'Admin Raízes', 'admin@raizes.com',
     '$2a$10$M8eAXMQHbtQ7l8ixrYCfK.tfxcX56oVQH.iFEf3cqCNmLnLG83Vae',
     'ADMIN', 1, '2026-01-01 00:00:00', '2026-01-01 00:00:00', 1);

-- Seed: fidelidade for admin
INSERT OR IGNORE INTO fidelidade (id, usuario_id, pontos, atualizado_em) VALUES
    (1, 1, 0, '2026-01-01 00:00:00');
