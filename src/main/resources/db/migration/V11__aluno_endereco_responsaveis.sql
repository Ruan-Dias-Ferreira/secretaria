-- Aluno: novos campos diretos
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS titulo_eleitor VARCHAR(50);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS telefone_responsavel VARCHAR(30);
ALTER TABLE aluno ALTER COLUMN rg DROP NOT NULL;
ALTER TABLE aluno ALTER COLUMN telefone DROP NOT NULL;
ALTER TABLE aluno ALTER COLUMN email DROP NOT NULL;

-- Drop UNIQUE constraints (rg/email opcionais, podem repetir vazio/null)
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

-- Endereço split
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS rua VARCHAR(255);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS bairro VARCHAR(255);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS cidade VARCHAR(255);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS estado VARCHAR(100);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS cep VARCHAR(20);

-- Backfill: mover endereco antigo p/ rua e dropar
UPDATE aluno SET rua = endereco WHERE rua IS NULL AND endereco IS NOT NULL;
ALTER TABLE aluno DROP COLUMN IF EXISTS endereco;

-- Mãe (backfill nome_mae)
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS mae_nome VARCHAR(255);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS mae_cpf VARCHAR(20);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS mae_rg VARCHAR(50);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS mae_titulo_eleitor VARCHAR(50);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS mae_telefone VARCHAR(30);
UPDATE aluno SET mae_nome = nome_mae WHERE mae_nome IS NULL AND nome_mae IS NOT NULL;
ALTER TABLE aluno DROP COLUMN IF EXISTS nome_mae;

-- Pai (backfill nome_pai)
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS pai_nome VARCHAR(255);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS pai_cpf VARCHAR(20);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS pai_rg VARCHAR(50);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS pai_titulo_eleitor VARCHAR(50);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS pai_telefone VARCHAR(30);
UPDATE aluno SET pai_nome = nome_pai WHERE pai_nome IS NULL AND nome_pai IS NOT NULL;
ALTER TABLE aluno DROP COLUMN IF EXISTS nome_pai;

-- Responsável legal
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS legal_nome VARCHAR(255);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS legal_cpf VARCHAR(20);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS legal_rg VARCHAR(50);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS legal_titulo_eleitor VARCHAR(50);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS legal_telefone VARCHAR(30);

-- Certidão de nascimento
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS certidao_matricula VARCHAR(40);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS certidao_livro VARCHAR(30);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS certidao_folha VARCHAR(30);
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS certidao_termo VARCHAR(30);
