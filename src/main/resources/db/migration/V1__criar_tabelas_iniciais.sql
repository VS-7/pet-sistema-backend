CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL
);

CREATE TABLE projetos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    tutor_id BIGINT NOT NULL REFERENCES usuarios(id),
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL
);

CREATE TABLE projeto_participantes (
    projeto_id BIGINT NOT NULL REFERENCES projetos(id),
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    PRIMARY KEY (projeto_id, usuario_id)
);

CREATE TABLE documentos (
    id BIGSERIAL PRIMARY KEY,
    projeto_id BIGINT NOT NULL REFERENCES projetos(id),
    tipo VARCHAR(20) NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    conteudo JSONB,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL
);

CREATE TABLE logs_atividades (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    entidade VARCHAR(50) NOT NULL,
    entidade_id BIGINT NOT NULL,
    acao VARCHAR(20) NOT NULL,
    detalhes JSONB,
    data_acao TIMESTAMP NOT NULL
);

CREATE TABLE certificados (
    id BIGSERIAL PRIMARY KEY,
    projeto_id BIGINT NOT NULL REFERENCES projetos(id),
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    codigo VARCHAR(255) NOT NULL UNIQUE,
    qr_code TEXT NOT NULL,
    data_emissao TIMESTAMP NOT NULL,
    data_inicio_projeto TIMESTAMP NOT NULL,
    data_fim_projeto TIMESTAMP NOT NULL,
    descricao_atividades TEXT NOT NULL,
    carga_horaria INTEGER NOT NULL,
    CONSTRAINT uk_certificado_projeto_usuario UNIQUE (projeto_id, usuario_id)
);

CREATE TABLE notificacoes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    titulo VARCHAR(255) NOT NULL,
    mensagem TEXT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    data_envio TIMESTAMP NOT NULL,
    data_leitura TIMESTAMP,
    CONSTRAINT fk_notificacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_projetos_tutor ON projetos(tutor_id);
CREATE INDEX idx_documentos_projeto ON documentos(projeto_id);
CREATE INDEX idx_logs_usuario ON logs_atividades(usuario_id);
CREATE INDEX idx_logs_entidade ON logs_atividades(entidade, entidade_id);
CREATE INDEX idx_certificados_codigo ON certificados(codigo);
CREATE INDEX idx_certificados_usuario ON certificados(usuario_id);
CREATE INDEX idx_certificados_projeto ON certificados(projeto_id);
CREATE INDEX idx_notificacoes_usuario ON notificacoes(usuario_id);
CREATE INDEX idx_notificacoes_lida ON notificacoes(lida);
CREATE INDEX idx_notificacoes_data_envio ON notificacoes(data_envio); 