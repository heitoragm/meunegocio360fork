-- RF-BE-003: clientes da empresa ativa. Toda linha pertence a uma empresa (RNF-096, RNF-BE-26).
CREATE TABLE cliente (
    id            BINARY(16)   NOT NULL,
    empresa_id    BINARY(16)   NOT NULL,
    tipo_pessoa   VARCHAR(10)  NOT NULL,
    -- Sem distinção de maiúsculas e acentos na busca e na ordenação (RN-08, RNF-BE-26).
    nome          VARCHAR(150) COLLATE utf8mb4_0900_ai_ci NOT NULL,
    documento     VARCHAR(14)  NOT NULL,
    email         VARCHAR(160) NULL,
    telefone      VARCHAR(11)  NULL,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     DATETIME(6)  NOT NULL,
    atualizado_em DATETIME(6)  NOT NULL,
    versao        BIGINT       NOT NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id),
    CONSTRAINT fk_cliente_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    -- Documento único por empresa, ativos e inativos (RN-03).
    CONSTRAINT uk_cliente_empresa_documento UNIQUE (empresa_id, documento),
    CONSTRAINT ck_cliente_tipo_pessoa CHECK (tipo_pessoa IN ('FISICA', 'JURIDICA')),
    -- Documento só com dígitos e no tamanho do tipo de pessoa (RN-02, RNF-BE-27).
    CONSTRAINT ck_cliente_documento CHECK (
        (tipo_pessoa = 'FISICA' AND documento REGEXP '^[0-9]{11}$')
        OR (tipo_pessoa = 'JURIDICA' AND documento REGEXP '^[0-9]{14}$')),
    CONSTRAINT ck_cliente_telefone CHECK (telefone IS NULL OR telefone REGEXP '^[0-9]{10,11}$')
);

CREATE INDEX idx_cliente_empresa_nome ON cliente (empresa_id, nome);
CREATE INDEX idx_cliente_empresa_ativo ON cliente (empresa_id, ativo);
