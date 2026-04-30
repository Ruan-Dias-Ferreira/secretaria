-- ============================================================
-- SEED — usuarios (senha: "senha123", bcrypt 10 rounds)
-- ============================================================
INSERT INTO usuario (login, senha, role) VALUES
('secretaria@escola.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'SECRETARIA'),
('prof.carlos@escola.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROFESSOR'),
('prof.ana@escola.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROFESSOR'),
('prof.joao@escola.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROFESSOR');

-- ============================================================
-- TURMAS
-- ============================================================
INSERT INTO turma (nome, ano_letivo, turno, curso) VALUES
('1A - Informatica',   2024, 'MANHA', 'Tecnico em Informatica'),
('2B - Administracao', 2024, 'TARDE',  'Tecnico em Administracao'),
('3C - Informatica',   2024, 'NOITE',  'Tecnico em Informatica');

-- ============================================================
-- DISCIPLINAS (professor_id refs: prof.carlos=2, prof.ana=3, prof.joao=4)
-- ============================================================
INSERT INTO disciplina (nome, carga_horaria, turma_id, professor_id) VALUES
('Matematica',             80, 1, 2),
('Portugues',              80, 1, 3),
('Programacao Web',        60, 1, 2),
('Contabilidade',          80, 2, 4),
('Marketing',              60, 2, 3),
('Banco de Dados',         60, 3, 2),
('Redes de Computadores',  60, 3, 4);

-- ============================================================
-- ALUNOS (10 alunos)
-- ============================================================
INSERT INTO aluno (nome, rg, cpf, data_nascimento, email, telefone, endereco, nome_mae, nome_pai) VALUES
('Lucas Ferreira Silva',      '12.345.678-9', '111.222.333-01', '2007-03-15', 'lucas.ferreira@email.com',    '(11)91111-0001', 'Rua das Flores, 10',     'Maria Ferreira',   'Jose Ferreira'),
('Ana Carolina Souza',        '23.456.789-0', '222.333.444-02', '2007-07-22', 'ana.souza@email.com',         '(11)91111-0002', 'Av. Brasil, 200',        'Lucia Souza',      'Pedro Souza'),
('Rafael Oliveira Costa',     '34.567.890-1', '333.444.555-03', '2006-11-08', 'rafael.oliveira@email.com',   '(11)91111-0003', 'Rua Palmeira, 45',       'Sandra Oliveira',  'Roberto Oliveira'),
('Beatriz Lima Alves',        '45.678.901-2', '444.555.666-04', '2007-01-30', 'beatriz.lima@email.com',      '(11)91111-0004', 'Travessa do Sol, 7',     'Claudia Lima',     'Antonio Lima'),
('Gustavo Pereira Martins',   '56.789.012-3', '555.666.777-05', '2006-05-12', 'gustavo.pereira@email.com',   '(11)91111-0005', 'Rua das Acaias, 33',     'Patricia Pereira', 'Marcos Pereira'),
('Fernanda Castro Rocha',     '67.890.123-4', '666.777.888-06', '2007-09-18', 'fernanda.castro@email.com',   '(11)91111-0006', 'Alameda Verde, 88',      'Renata Castro',    'Carlos Castro'),
('Thiago Gomes Barbosa',      '78.901.234-5', '777.888.999-07', '2006-02-25', 'thiago.gomes@email.com',      '(11)91111-0007', 'Rua Sete de Setembro, 5','Helena Gomes',     'Francisco Gomes'),
('Juliana Ribeiro Nunes',     '89.012.345-6', '888.999.000-08', '2007-06-04', 'juliana.ribeiro@email.com',   '(11)91111-0008', 'Praca da Paz, 12',       'Vera Ribeiro',     'Edilson Ribeiro'),
('Diego Nascimento Teles',    '90.123.456-7', '999.000.111-09', '2006-08-14', 'diego.nascimento@email.com',  '(11)91111-0009', 'Rua Nova, 67',           'Fatima Nascimento', NULL),
('Camila Araujo Mendes',      '01.234.567-8', '000.111.222-10', '2007-04-27', 'camila.araujo@email.com',     '(11)91111-0010', 'Rua das Mangueiras, 3',  'Silvia Araujo',    'Wilson Araujo');

-- ============================================================
-- MATRICULAS — situacoes diversas
-- 1:Lucas     ATIVA        turma 1
-- 2:Ana       ATIVA        turma 1
-- 3:Rafael    ATIVA        turma 2
-- 4:Beatriz   CONCLUIDA    turma 2
-- 5:Gustavo   DESISTENTE   turma 1
-- 6:Fernanda  TRANSFERIDO  turma 1
-- 7:Thiago    ATIVA        turma 3
-- 8:Juliana   ATIVA        turma 3
-- 9:Diego     DESISTENTE   turma 3
-- 10:Camila   TRANSFERIDO  turma 2
-- ============================================================
INSERT INTO matricula (ano_letivo, status, aluno_id, turma_id) VALUES
(2024, 'ATIVA',       1,  1),
(2024, 'ATIVA',       2,  1),
(2024, 'ATIVA',       3,  2),
(2024, 'CONCLUIDA',   4,  2),
(2024, 'DESISTENTE',  5,  1),
(2024, 'TRANSFERIDO', 6,  1),
(2024, 'ATIVA',       7,  3),
(2024, 'ATIVA',       8,  3),
(2024, 'DESISTENTE',  9,  3),
(2024, 'TRANSFERIDO', 10, 2);

-- ============================================================
-- NOTAS — alunos ativos e concluido (disciplinas da turma)
-- Turma 1: disciplinas 1(Mat),2(Port),3(ProgWeb) — alunos 1,2
-- Turma 2: disciplinas 4(Cont),5(Mkt)            — aluno 3, 4(concluida)
-- Turma 3: disciplinas 6(BD),7(Redes)            — alunos 7,8
-- ============================================================
INSERT INTO nota (bimestre, valor, situacao, aluno_id, disciplina_id) VALUES
-- Lucas (ativo, turma 1)
(1, 8.50, 'APROVADO', 1, 1), (2, 7.00, 'APROVADO', 1, 1),
(1, 9.00, 'APROVADO', 1, 2), (2, 8.50, 'APROVADO', 1, 2),
(1, 7.50, 'APROVADO', 1, 3), (2, 6.00, 'APROVADO', 1, 3),
-- Ana (ativa, turma 1)
(1, 5.00, 'RECUPERACAO', 2, 1), (2, 6.50, 'APROVADO', 2, 1),
(1, 8.00, 'APROVADO',    2, 2), (2, 8.00, 'APROVADO', 2, 2),
(1, 4.00, 'REPROVADO',   2, 3), (2, 3.50, 'REPROVADO', 2, 3),
-- Rafael (ativo, turma 2)
(1, 7.00, 'APROVADO', 3, 4), (2, 7.50, 'APROVADO', 3, 4),
(1, 6.00, 'APROVADO', 3, 5), (2, 6.50, 'APROVADO', 3, 5),
-- Beatriz (concluida, turma 2)
(1, 9.00, 'APROVADO', 4, 4), (2, 9.50, 'APROVADO', 4, 4),
(1, 8.50, 'APROVADO', 4, 5), (2, 8.00, 'APROVADO', 4, 5),
-- Thiago (ativo, turma 3)
(1, 7.00, 'APROVADO', 7, 6), (2, 7.50, 'APROVADO', 7, 6),
(1, 6.50, 'APROVADO', 7, 7), (2, 7.00, 'APROVADO', 7, 7),
-- Juliana (ativa, turma 3)
(1, 8.00, 'APROVADO', 8, 6), (2, 9.00, 'APROVADO', 8, 6),
(1, 5.00, 'RECUPERACAO', 8, 7), (2, 6.00, 'APROVADO', 8, 7);

-- ============================================================
-- FREQUENCIA — amostras para alunos ativos
-- ============================================================
INSERT INTO frequencia (data, presente, aluno_id, disciplina_id) VALUES
-- Lucas
('2024-02-05', true,  1, 1), ('2024-02-12', true,  1, 1), ('2024-02-19', false, 1, 1),
('2024-02-05', true,  1, 2), ('2024-02-12', true,  1, 2), ('2024-02-19', true,  1, 2),
-- Ana
('2024-02-05', true,  2, 1), ('2024-02-12', false, 2, 1), ('2024-02-19', false, 2, 1),
('2024-02-05', true,  2, 2), ('2024-02-12', true,  2, 2), ('2024-02-19', false, 2, 2),
-- Rafael
('2024-02-05', true,  3, 4), ('2024-02-12', true,  3, 4), ('2024-02-19', true,  3, 4),
-- Thiago
('2024-02-05', true,  7, 6), ('2024-02-12', false, 7, 6), ('2024-02-19', true,  7, 6),
-- Juliana
('2024-02-05', true,  8, 6), ('2024-02-12', true,  8, 6), ('2024-02-19', true,  8, 6),
('2024-02-05', true,  8, 7), ('2024-02-12', true,  8, 7), ('2024-02-19', false, 8, 7);

-- ============================================================
-- DOCUMENTOS — alguns alunos
-- ============================================================
INSERT INTO documento (tipo, data_emissao, aluno_id) VALUES
('DECLARACAO_MATRICULA',  '2024-02-01', 1),
('HISTORICO_ESCOLAR',     '2024-02-01', 4),
('DECLARACAO_TRANSFERENCIA', '2024-06-15', 6),
('DECLARACAO_TRANSFERENCIA', '2024-07-01', 10),
('DECLARACAO_MATRICULA',  '2024-02-01', 7);
