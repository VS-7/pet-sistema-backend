System Design do Sistema PET (Plano de Estudo Tutorial)

Este documento detalha o System Design para o backend do sistema de gerenciamento do Plano de Estudo Tutorial (PET). O sistema é monolítico modular e inclui funcionalidades para gerenciar projetos, criar documentos acadêmicos, estabelecer associações hierárquicas entre projetos e emitir certificados digitais.

Arquitetura do Sistema

Estilo Arquitetural

Monolito Modular:

Organização interna por módulos:

Gestão de Usuários

Gestão de Projetos

Gestão de Documentos

Associações entre Projetos

Emissão de Certificados

Logs e Histórico

Banco de Dados

Relacional (PostgreSQL):

Utilizado para armazenar dados principais.

users:

id, nome, email, senha, role (petiano, tutor).

projects:

id, titulo, descricao, status (em desenvolvimento, concluído), tutor_id.

documents:

id, projeto_id (FK), tipo (Ensino, Pesquisa, Extensão), conteudo (JSONB).

logs:

id, projeto_id (FK), user_id (FK), acao, data.

Grafo (MongoDB):

Para gerenciar associações e hierarquias entre projetos.

Nós:

Project: Representa um projeto.

Propriedades: id, titulo, tipo, status, tutor_id.

Relações:

REFERENCES: Ligação entre projetos.

Propriedades: tipo, data.

Funcionalidades

1. Gestão de Usuários

Funcionalidades:

Registro e autenticação de petianos e tutores.

Controle de acesso baseado em permissões:

Petianos: Criar e gerenciar projetos/documentos.

Tutores: Revisar, aprovar e emitir certificados.

Endpoints:

POST /auth/register: Registrar um usuário.

POST /auth/login: Autenticar um usuário.

GET /users/{id}/projects: Listar projetos do usuário.

GET /users/{id}/history: Consultar histórico de ações.

2. Gestão de Projetos

Funcionalidades:

CRUD completo de projetos.

Registro de metadados do projeto (título, descrição, status, participantes).

Endpoints:

POST /projects: Criar um novo projeto.

GET /projects/{id}: Consultar detalhes de um projeto.

PATCH /projects/{id}: Atualizar informações do projeto.

DELETE /projects/{id}: Excluir um projeto.

GET /projects/{id}/logs: Consultar histórico de alterações.

3. Gestão de Documentos

Funcionalidades:

Criação e edição de documentos diretamente no sistema.

Modelos pré-definidos para Ensino, Pesquisa e Extensão.

Geração de PDF customizado com formatação acadêmica.

Endpoints:

POST /projects/{id}/documents: Criar um documento.

GET /projects/{id}/documents: Visualizar documento associado ao projeto.

PATCH /documents/{id}: Atualizar conteúdo do documento.

POST /documents/{id}/generate-pdf: Gerar PDF do documento.

4. Associações entre Projetos

Funcionalidades:

Relacionar projetos derivados ou referenciados.

Visualizar hierarquia e conexões no estilo gráfico (GitHub).

Bem como vizualizar a hierarquia no estilo de uma lista também.

Consultar histórico de associações.

Endpoints:

POST /projects/{id}/associations: Criar associação entre dois projetos.

GET /projects/{id}/associations: Listar projetos associados.

GET /projects/{id}/associations/history: Consultar histórico de associações.

5. Emissão de Certificados

Funcionalidades:

Geração automática de certificados em PDF.

Gerar QRCode para validação



Endpoints:

POST /certificates: Gerar certificado para um projeto.

GET /certificates/{id}: Baixar certificado emitido.

6. Logs e Histórico

Funcionalidades:

Registro completo de ações realizadas em projetos.

Consulta de alterações por projeto ou usuário.

Endpoints:

GET /projects/{id}/logs: Consultar histórico de alterações.

GET /users/{id}/history: Consultar histórico de ações realizadas por um usuário.

Tecnologias

Backend

Spring Boot: Framework para desenvolvimento do backend.

Spring Data JPA: Integração com PostgreSQL.

Hibernate: ORM para o banco relacional.

Spring Security: Autenticação e autorização (JWT).

MongoDB: Para modelagem de associações complexas entre projetos.

Geração de Documentos e Certificados

Thymeleaf: Templates para modelar documentos.

iText: Para exportação de PDFs.

Banco de Dados

PostgreSQL: Dados relacionais.

MongoDB: Gerenciamento de associações como grafo.

Outras Ferramentas

Docker: Containerização do sistema.

Flyway: Migração de esquemas do banco.

Redis (opcional): Cache para consultas frequentes.

Fluxo de Trabalho (E2E)

Registro de Usuários:

Usuários se registram e fazem login.

Criação de Projetos:

Petianos criam projetos com metadados iniciais.

Associação entre Projetos:

Projetos são relacionados em uma estrutura hierárquica ou de referência.

Criação e Edição de Documentos:

Petianos criam e editam documentos diretamente na plataforma.

Revisão e Aprovação:

Tutores revisam e aprovam documentos.

Geração de Certificados:

Sistema emite certificados digitais com QR Code.

Consulta de Histórico:

Logs de ações ficam disponíveis para consulta.

Benefícios do Design

Integração Completa:

Todas as funcionalidades em um único backend monolítico.

Escalabilidade:

Banco de grafos permite expansão futura.

Facilidade de Manutenção:

Sistema modularizado facilita implementações adicionais.

Experiência do Usuário:

Interface intuitiva com suporte a visualizações hierárquicas