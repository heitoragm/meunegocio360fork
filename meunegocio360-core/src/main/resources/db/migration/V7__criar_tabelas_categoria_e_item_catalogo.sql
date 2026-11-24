-- RF-BE-005: catálogo único de produtos e serviços, com categorias opcionais.

CREATE TABLE categoria (
    id            BINARY(16)  NOT NULL,
    empresa_id    BINARY(16)  NOT NULL,
    -- A collation faz "Informática" e "informatica" colidirem na unicidade (RN-06, RNF-BE-26).
    nome          VARCHAR(80) COLLATE utf8mb4_0900_ai_ci NOT NULL,
    ativo         BOOLEAN     NOT NULL DEFAULT TRUE,
    criado_em     DATETIME(6) NOT NULL,
    atualizado_em DATETIME(6) NOT NULL,
    versao        BIGINT      NOT NULL,
    CONSTRAINT pk_categoria PRIMARY KEY (id),
    CONSTRAINT fk_categoria_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uk_categoria_empresa_nome UNIQUE (empresa_id, nome)
);

CREATE TABLE item_catalogo (
    id             BINARY(16)    NOT NULL,
    empresa_id     BINARY(16)    NOT NULL,
    categoria_id   BINARY(16)    NULL,
    tipo           VARCHAR(10)   NOT NULL,
    codigo         VARCHAR(40)   COLLATE utf8mb4_0900_ai_ci NULL,
    nome           VARCHAR(150)  COLLATE utf8mb4_0900_ai_ci NOT NULL,
    descricao      VARCHAR(500)  NULL,
    unidade        VARCHAR(6)    NOT NULL,
    preco          DECIMAL(12,2) NOT NULL,
    -- RN-07: existe no modelo desta fase para uso futuro de Suprimentos; nenhum DTO o expõe.
    estoque_minimo DECIMAL(15,3) NOT NULL DEFAULT 0,
    ativo          BOOLEAN       NOT NULL DEFAULT TRUE,
    criado_em      DATETIME(6)   NOT NULL,
    atualizado_em  DATETIME(6)   NOT NULL,
    versao         BIGINT        NOT NULL,
    CONSTRAINT pk_item_catalogo PRIMARY KEY (id),
    CONSTRAINT fk_item_catalogo_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    -- Sem cascata: categoria com itens não pode ser excluída (RN-06).
    CONSTRAINT fk_item_catalogo_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    -- Código único por empresa quando informado; o MySQL aceita vários NULL (RN-04).
    CONSTRAINT uk_item_catalogo_empresa_codigo UNIQUE (empresa_id, codigo),
    CONSTRAINT ck_item_catalogo_tipo CHECK (tipo IN ('PRODUTO', 'SERVICO')),
    CONSTRAINT ck_item_catalogo_preco CHECK (preco >= 0),
    CONSTRAINT ck_item_catalogo_estoque_minimo CHECK (estoque_minimo >= 0)
);

CREATE INDEX idx_item_catalogo_categoria ON item_catalogo (categoria_id);
CREATE INDEX idx_item_catalogo_empresa_tipo_ativo ON item_catalogo (empresa_id, tipo, ativo);
CREATE INDEX idx_item_catalogo_empresa_nome ON item_catalogo (empresa_id, nome);
