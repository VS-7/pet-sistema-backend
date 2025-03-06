-- Adiciona a coluna pet_id
ALTER TABLE projetos
ADD COLUMN pet_id BIGINT;

-- Adiciona a constraint de chave estrangeira
ALTER TABLE projetos
ADD CONSTRAINT fk_projeto_pet
FOREIGN KEY (pet_id)
REFERENCES pets (id);

-- Depois que todos os projetos forem associados a PETs, podemos tornar a coluna NOT NULL
-- ALTER TABLE projetos ALTER COLUMN pet_id SET NOT NULL; 