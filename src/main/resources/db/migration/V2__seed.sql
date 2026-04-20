-- Seed: unidades
INSERT INTO unidade (nome, cidade, estado) VALUES
    ('Raízes Fortaleza Centro', 'Fortaleza', 'CE'),
    ('Raízes Recife Boa Viagem', 'Recife', 'PE'),
    ('Raízes Salvador Pelourinho', 'Salvador', 'BA');

-- Seed: produtos
INSERT INTO produto (nome, descricao, preco, categoria) VALUES
    ('Baião de Dois', 'Arroz com feijão de corda, queijo coalho e bacon', 32.90, 'Prato Principal'),
    ('Carne de Sol na Brasa', 'Carne de sol grelhada com manteiga de garrafa', 45.90, 'Prato Principal'),
    ('Tapioca Recheada', 'Tapioca com queijo e carne seca', 18.90, 'Lanche'),
    ('Suco de Cajá', 'Suco natural de cajá 500ml', 9.90, 'Bebida'),
    ('Pamonha Doce', 'Pamonha artesanal de milho verde', 8.50, 'Sobremesa');

-- Seed: estoque por unidade
INSERT INTO estoque (unidade_id, produto_id, quantidade) VALUES
    (1, 1, 50), (1, 2, 30), (1, 3, 80), (1, 4, 100), (1, 5, 60),
    (2, 1, 40), (2, 2, 25), (2, 3, 70), (2, 4, 90),  (2, 5, 50),
    (3, 1, 35), (3, 2, 20), (3, 3, 60), (3, 4, 80),  (3, 5, 45);

-- Seed: admin
INSERT INTO usuario (nome, email, senha, role, consentimento_lgpd, data_consentimento) VALUES
    ('Admin Raízes', 'admin@raizes.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y',
     'ADMIN', 1, CURRENT_TIMESTAMP);
-- senha: admin123

INSERT INTO fidelidade (usuario_id, pontos) VALUES (1, 0);
