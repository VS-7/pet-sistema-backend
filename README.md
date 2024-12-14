# 🎓 PET Sistema Backend

Sistema de gerenciamento do Plano de Estudo Tutorial (PET), uma plataforma robusta desenvolvida com Spring Boot 3.x e Java 21 para gerenciar projetos acadêmicos, documentos e certificações.

## 📋 Sumário
- [Sobre o Projeto](#-sobre-o-projeto)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Instalação](#-instalação)
- [Uso da API](#-uso-da-api)
- [Documentação da API](#-documentação-da-api)
- [Contribuição](#-contribuição)

## 🎯 Sobre o Projeto

O PET Sistema é uma plataforma completa que oferece:

- **Gestão de Projetos Acadêmicos**
  - Criação e gerenciamento de projetos
  - Associação de participantes
  - Acompanhamento de status

- **Documentação Digital**
  - Criação de documentos estruturados
  - Suporte a diferentes tipos (Ensino, Pesquisa, Extensão)
  - Versionamento de conteúdo

- **Certificação Digital**
  - Emissão automatizada de certificados
  - QR Code para validação
  - Sistema de verificação online

- **Sistema de Notificações**
  - Notificações em tempo real
  - Integração com email
  - Centro de notificações personalizado

## 🚀 Tecnologias

### Core
- Java 21
- Spring Boot 3.x
- Spring Security
- Spring Data JPA/MongoDB

### Banco de Dados
- PostgreSQL (dados principais)
- MongoDB (associações)
- Redis (cache)

### Ferramentas
- Docker & Docker Compose
- Maven
- Flyway (migrations)
- JWT (autenticação)
- iText (PDFs)
- JavaMail

## 🏗 Arquitetura

```plaintext
src/
├── main/
│ ├── java/
│ │ └── com/ifcolab/pet_sistema_backend/
│ │ ├── config/ # Configurações
│ │ ├── controller/ # Controllers REST
│ │ ├── dto/ # DTOs
│ │ ├── exception/ # Exceções
│ │ ├── model/ # Entidades
│ │ ├── repository/ # Repositórios
│ │ ├── security/ # Segurança
│ │ └── service/ # Serviços
│ └── resources/
│ ├── db/migration/ # Migrações
│ └── templates/ # Templates
```

## 🛠 Instalação

### Pré-requisitos
- Java 21
- Docker e Docker Compose
- Maven

### Passo a Passo

1. **Clone o repositório**
```bash
git clone https://github.com/seu-usuario/pet-sistema-backend.git
cd pet-sistema-backend
```

2. **Configure o ambiente**

```bash
export JWT_SECRET=sua-chave-secreta
export MAIL_USERNAME=seu-email@gmail.com
export MAIL_PASSWORD=sua-senha
```

3. **Execute o projeto**

```bash
mvn spring-boot:run
```

## 📡 Uso da API

### 1. Autenticação

**Registro de Usuário**

```bash
curl -X POST http://localhost:8080/api/v1/auth/registrar \
-H "Content-Type: application/json" \
-d '{
  "nome": "João Silva",
  "email": "joao.silva@example.com",
  "senha": "senha123",
  "tipo": "TUTOR"
}'
```
**Login**

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
-H "Content-Type: application/json" \
-d '{
  "email": "joao.silva@example.com",
  "senha": "senha123"
}'
```
### 2. Projetos

**Criar Projeto**

```bash
curl -X POST http://localhost:8080/api/v1/projetos \
-H "Authorization: Bearer {token}" \
-H "Content-Type: application/json" \
-d '{
  "nome": "Novo Projeto",
  "descricao": "Descrição do projeto"
}'
```

### 3. Documentos

**Criar Documento**

```bash
curl -X POST http://localhost:8080/api/v1/documentos \
-H "Authorization: Bearer $TOKEN" \
-H "Content-Type: application/json" \
-d '{
  "projetoId": 1,
  "tipo": "PESQUISA",
  "titulo": "Relatório Final",
  "conteudo": {
    "introducao": "Texto introdutório",
    "metodologia": "Metodologia utilizada",
    "resultados": "Resultados obtidos",
    "conclusao": "Conclusões finais"
  }
}'
```
## 📚 Documentação da API

### Endpoints Principais

#### Autenticação
- `POST /api/v1/auth/registrar` - Registro de usuário
- `POST /api/v1/auth/login` - Login
- `GET /api/v1/auth/me` - Dados do usuário atual

#### Projetos
- `POST /api/v1/projetos` - Criar projeto
- `GET /api/v1/projetos` - Listar projetos
- `GET /api/v1/projetos/{id}` - Detalhes do projeto
- `PUT /api/v1/projetos/{id}` - Atualizar projeto
- `DELETE /api/v1/projetos/{id}` - Excluir projeto

#### Documentos
- `POST /api/v1/documentos` - Criar documento
- `GET /api/v1/documentos` - Listar documentos
- `GET /api/v1/documentos/{id}` - Visualizar documento
- `PUT /api/v1/documentos/{id}` - Atualizar documento

#### Certificados
- `POST /api/v1/certificados` - Emitir certificado
- `GET /api/v1/certificados/{id}` - Visualizar certificado
- `GET /api/v1/certificados/{id}/download` - Download do PDF

## 🔐 Segurança

O sistema implementa:
- Autenticação JWT
- Autorização baseada em roles
- Proteção contra CSRF
- CORS configurável
- Senha com hash BCrypt

## 📊 Funcionalidades Principais

### 1. Gestão de Usuários
- Registro e autenticação
- Perfis: PETIANO e TUTOR
- Controle de acesso por role

### 2. Gestão de Projetos
- CRUD completo
- Associações hierárquicas
- Log de alterações

### 3. Documentação
- Conteúdo estruturado em JSON
- Tipos: ENSINO, PESQUISA, EXTENSAO
- Sistema de templates

### 4. Certificação
- Geração automática de PDF
- QR Code para validação
- Sistema de verificação

### 5. Notificações
- Tempo real
- Email
- Centro de notificações
