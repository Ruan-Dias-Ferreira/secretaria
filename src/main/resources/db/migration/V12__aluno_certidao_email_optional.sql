-- Email opcional + sem unique
ALTER TABLE aluno ALTER COLUMN email DROP NOT NULL;

-- Drop UNIQUE em rg e email (podem repetir vazio/null)
DO $$
DECLARE r record;
BEGIN
    FOR r IN
        SELECT conname FROM pg_constraint
        WHERE conrelid = 'aluno'::regclass
          AND contype = 'u'
          AND pg_get_constraintdef(oid) ~* '\(rg\)|\(email\)'
    LOOP
        EXECUTE 'ALTER TABLE aluno DROP CONSTRAINT ' || quote_ident(r.conname);
    END LOOP;
END$$;

-- Certidão de nascimento
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS certidao_matricula VARCHAR(40);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS certidao_livro VARCHAR(30);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS certidao_folha VARCHAR(30);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS certidao_termo VARCHAR(30);
