-- RF-BE-004: fornecedores da empresa ativa. Mesmas colunas de cliente, mas cadastro independente:
-- não há chave estrangeira entre cliente e fornecedor, e o mesmo documento pode existir nos dois.
CREATE TABLE fornecedor (
    id            BINARY(16)   NOT NULL,
    empresa_id    BINARY(16)   NOT NULL,
    tipo_pessoa   VARCHAR(10)  NOT NULL,
    -- Sem distinção de maiúsculas e acentos na busca e na ordenação (RNF-BE-26).
    nome          VARCHAR(150) COLLATE utf8mb4_0900_ai_ci NOT NULL,
    documento     VARCHAR(14)  NOT NULL,
    email         VARCHAR(160) NULL,
    telefone      VARCHAR(11)  NULL,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     DATETIME(6)  NOT NULL,
    atualizado_em DATETIME(6)  NOT NULL,
    versao        BIGINT       NOT NULL,
    CONSTRAINT pk_fornecedor PRIMARY KEY (id),
    CONSTRAINT fk_fornecedor_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    -- Documento único por empresa, ativos e inativos (RN-02).
    CONSTRAINT uk_fornecedor_empresa_documento UNIQUE (empresa_id, documento),
    CONSTRAINT ck_fornecedor_tipo_pessoa CHECK (tipo_pessoa IN ('FISICA', 'JURIDICA')),
    -- Documento só com dígitos e no tamanho do tipo de pessoa (RNF-BE-27).
    CONSTRAINT ck_fornecedor_documento CHECK (
        (tipo_pessoa = 'FISICA' AND documento REGEXP '^[0-9]{11}$')
        OR (tipo_pessoa = 'JURIDICA' AND documento REGEXP '^[0-9]{14}$')),
    CONSTRAINT ck_fornecedor_telefone CHECK (telefone IS NULL OR telefone REGEXP '^[0-9]{10,11}$')
);

CREATE INDEX idx_fornecedor_empresa_nome ON fornecedor (empresa_id, nome);
CREATE INDEX idx_fornecedor_empresa_ativo ON fornecedor (empresa_id, ativo);
