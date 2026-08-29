# Secretaria Escolar — API

API REST para a rotina de uma secretaria escolar: matrícula e rematrícula de alunos, turmas, disciplinas, notas, frequência, documentos e eventos — com controle de acesso por perfil.

O projeto nasceu da prática. Trabalhei três anos como auxiliar de secretaria em uma escola estadual, onde cadastro, conferência de documentos e controle de frequência eram feitos em planilhas e papel. Este sistema é a tentativa de resolver, em código, os problemas que eu via todo dia.

**Front-end da aplicação:** [secretaria-frontend](https://github.com/Ruan-Dias-Ferreira/secretaria-frontend) (Angular)

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Persistência | Spring Data JPA · PostgreSQL 16 |
| Migrações | Flyway |
| Segurança | Spring Security · JWT (java-jwt) |
| Mapeamento | MapStruct · Lombok |
| Validação | Bean Validation |
| Testes | JUnit · Testcontainers |
| Build | Maven (wrapper incluso) |

## Como rodar

Pré-requisitos: **JDK 21** e **Docker**.

```bash
# 1. copie o exemplo de variáveis de ambiente e ajuste se quiser
cp .env.example .env

# 2. sobe o PostgreSQL 16
docker compose up -d

# 3. sobe a aplicação — o Flyway aplica as migrações automaticamente
./mvnw spring-boot:run
```

A API fica disponível em `http://localhost:8080`.

```bash
# testes de integração (o Testcontainers sobe um Postgres descartável)
./mvnw test
```

## Arquitetura

Organização em camadas, com o pacote base `com.escola.secretaria`:

```
controller/   endpoints REST, um por recurso
service/      regras de negócio
repository/   Spring Data JPA
domain/       entidades e enums
dto/          contratos de entrada e saída da API
mapper/       conversão entidade e DTO (MapStruct)
security/     autenticação JWT e autorização por perfil
exception/    tratamento centralizado de erros
```

O banco é versionado com Flyway em `src/main/resources/db/migration` — hoje são 14 migrações, da criação inicial até campos de perfil do aluno, rematrícula e eventos. Nenhuma alteração de schema é feita à mão.

## Domínio

`Aluno` · `Responsavel` · `Endereco` · `CertidaoNascimento` · `Matricula` · `RematriculaInfo` · `Turma` · `Disciplina` · `Nota` · `Frequencia` · `MotivoFalta` · `Documento` · `Evento` · `TipoEvento` · `Usuario`

## Módulos da API

| Recurso | O que faz |
|---|---|
| `/auth` | Autenticação e emissão do token JWT |
| `/aluno` | Cadastro, edição, situação do aluno, frequências e resumo de pendências |
| `/matricula` | Matrícula e rematrícula no ano letivo |
| `/turma` · `/disciplina` | Turmas, disciplinas e vínculo com professores |
| `/nota` · `/frequencia` | Lançamento de notas e registro de presença com motivo de falta |
| `/documento` | Documentos do aluno e controle de pendências |
| `/evento` | Agenda escolar |
| `/usuario` | Usuários do sistema e seus perfis |

## Segurança

Autenticação por JWT: o token é emitido no login e validado a cada requisição. A autorização é por perfil, declarada nos controllers com `@PreAuthorize` — a secretaria tem acesso amplo aos cadastros, enquanto o professor alcança apenas o que precisa, como a frequência das suas turmas.

As credenciais do banco em desenvolvimento vêm de variáveis de ambiente; veja `.env.example`.

## Status

Projeto pessoal em desenvolvimento contínuo, usado como estudo prático de Spring Boot, modelagem relacional e segurança de APIs.

---

**Ruan Dias Ferreira** — estudante de Análise e Desenvolvimento de Sistemas na UTFPR, câmpus Ponta Grossa.
[LinkedIn](https://www.linkedin.com/in/ruan-dias-ferreira-07606b3b3)
