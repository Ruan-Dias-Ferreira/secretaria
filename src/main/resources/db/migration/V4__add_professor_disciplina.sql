ALTER TABLE disciplina ADD COLUMN professor_id BIGINT REFERENCES usuario(id);
