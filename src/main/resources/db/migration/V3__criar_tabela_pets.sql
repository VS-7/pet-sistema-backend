CREATE TABLE pets (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    codigo VARCHAR(8) NOT NULL UNIQUE,
    descricao TEXT,
    tutor_id BIGINT NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_pet_tutor FOREIGN KEY (tutor_id) REFERENCES usuarios(id)
);

CREATE TABLE pet_membros (
    pet_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    PRIMARY KEY (pet_id, usuario_id),
    CONSTRAINT fk_pet_membro_pet FOREIGN KEY (pet_id) REFERENCES pets(id),
    CONSTRAINT fk_pet_membro_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
); 