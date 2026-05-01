-- Adiciona bloco "Rematriculado" ao Aluno (último ano letivo destino)
ALTER TABLE aluno
    ADD COLUMN rematriculado_ano_letivo INTEGER,
    ADD COLUMN rematriculado_serie      VARCHAR(50),
    ADD COLUMN rematriculado_turma      VARCHAR(100),
    ADD COLUMN rematriculado_turno      VARCHAR(20);
