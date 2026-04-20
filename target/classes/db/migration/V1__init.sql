CREATE TABLE usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    senha TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'CLIENTE',
    consentimento_lgpd INTEGER NOT NULL DEFAULT 0,
    data_consentimento TIMESTAMP,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE unidade (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    cidade TEXT NOT NULL,
    estado TEXT NOT NULL,
    ativa INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE produto (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    descricao TEXT,
    preco REAL NOT NULL,
    categoria TEXT NOT NULL,
    disponivel INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE estoque (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    unidade_id INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    quantidade INTEGER NOT NULL DEFAULT 0,
    UNIQUE(unidade_id, produto_id),
    FOREIGN KEY (unidade_id) REFERENCES unidade(id),
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE pedido (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER NOT NULL,
    unidade_id INTEGER NOT NULL,
    canal_pedido TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    total REAL NOT NULL DEFAULT 0,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (unidade_id) REFERENCES unidade(id)
);

CREATE TABLE item_pedido (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pedido_id INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario REAL NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE pagamento (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pedido_id INTEGER NOT NULL UNIQUE,
    forma_pagamento TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDENTE',
    valor REAL NOT NULL,
    payload_retorno TEXT,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processado_em TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id)
);

CREATE TABLE fidelidade (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER NOT NULL UNIQUE,
    pontos INTEGER NOT NULL DEFAULT 0,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE audit_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER,
    acao TEXT NOT NULL,
    recurso TEXT NOT NULL,
    ip_origem TEXT,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    detalhes TEXT
);
