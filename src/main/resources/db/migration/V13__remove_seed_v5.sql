-- ============================================================
-- Remove dados ficticios inseridos em V5__seed.sql
-- Mantem usuarios (login) intactos
-- Match: alunos por CPF, turmas por nome+ano_letivo=2024
-- ============================================================

-- ids dos alunos seed
WITH alunos_seed AS (
    SELECT id FROM aluno WHERE cpf IN (
        '111.222.333-01','222.333.444-02','333.444.555-03','444.555.666-04','555.666.777-05',
        '666.777.888-06','777.888.999-07','888.999.000-08','999.000.111-09','000.111.222-10'
    )
),
turmas_seed AS (
    SELECT id FROM turma WHERE ano_letivo = 2024 AND nome IN (
        '1A - Informatica','2B - Administracao','3C - Informatica'
    )
),
disciplinas_seed AS (
    SELECT id FROM disciplina WHERE turma_id IN (SELECT id FROM turmas_seed)
)
DELETE FROM frequencia
 WHERE aluno_id      IN (SELECT id FROM alunos_seed)
    OR disciplina_id IN (SELECT id FROM disciplinas_seed);

DELETE FROM nota
 WHERE aluno_id IN (SELECT id FROM aluno WHERE cpf IN (
        '111.222.333-01','222.333.444-02','333.444.555-03','444.555.666-04','555.666.777-05',
        '666.777.888-06','777.888.999-07','888.999.000-08','999.000.111-09','000.111.222-10'
    ));

DELETE FROM documento
 WHERE aluno_id IN (SELECT id FROM aluno WHERE cpf IN (
        '111.222.333-01','222.333.444-02','333.444.555-03','444.555.666-04','555.666.777-05',
        '666.777.888-06','777.888.999-07','888.999.000-08','999.000.111-09','000.111.222-10'
    ));

DELETE FROM matricula
 WHERE aluno_id IN (SELECT id FROM aluno WHERE cpf IN (
        '111.222.333-01','222.333.444-02','333.444.555-03','444.555.666-04','555.666.777-05',
        '666.777.888-06','777.888.999-07','888.999.000-08','999.000.111-09','000.111.222-10'
    ))
    OR turma_id IN (SELECT id FROM turma WHERE ano_letivo = 2024 AND nome IN (
        '1A - Informatica','2B - Administracao','3C - Informatica'
    ));

DELETE FROM disciplina
 WHERE turma_id IN (SELECT id FROM turma WHERE ano_letivo = 2024 AND nome IN (
        '1A - Informatica','2B - Administracao','3C - Informatica'
    ));

DELETE FROM aluno
 WHERE cpf IN (
        '111.222.333-01','222.333.444-02','333.444.555-03','444.555.666-04','555.666.777-05',
        '666.777.888-06','777.888.999-07','888.999.000-08','999.000.111-09','000.111.222-10'
    );

DELETE FROM turma
 WHERE ano_letivo = 2024
   AND nome IN ('1A - Informatica','2B - Administracao','3C - Informatica');
