# AlgaFood API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Docker](https://img.shields.io/badge/Docker-Supported-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 Descrição

**AlgaFood API** é uma API RESTful de delivery de comida desenvolvida com Spring Boot. O projeto oferece um sistema completo para gerenciamento de restaurantes, pedidos, produtos, formas de pagamento, usuários e muito mais.

A API segue as melhores práticas de desenvolvimento, incluindo:
- Arquitetura em camadas (Controller, Service, Repository)
- HATEOAS para navegabilidade
- Documentação OpenAPI/Swagger
- Autenticação OAuth2 com JWT
- Integração com serviços AWS (S3, SQS, EventBridge, SES)
- Migrations com Flyway
- Docker para containerização

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.0**
- **Spring Security** com OAuth2 Authorization Server
- **Spring Data JPA** / Hibernate
- **MySQL 8.0**
- **Redis** (Session Store)
- **Flyway** (Migrations)
- **Lombok**
- **ModelMapper**
- **JasperReports** (Relatórios PDF)
- **AWS SDK** (S3, SQS, EventBridge, SES)
- **Spring Cloud AWS 3.1.0**
- **LocalStack** (Emulação AWS local)
- **Docker / Docker Compose**
- **Nginx** (Reverse Proxy)
- **SpringDoc OpenAPI** (Swagger UI)

## 📁 Estrutura do Projeto

```
algafood/
├── src/
│   ├── main/
│   │   ├── java/com/algaworks/algafood/
│   │   │   ├── api/                    # Controllers, assemblers, modelos de representação
│   │   │   │   ├── v1/                 # API versão 1
│   │   │   │   │   ├── controller/     # Controllers REST
│   │   │   │   │   ├── assembler/      # Conversores DTO <-> Entity
│   │   │   │   │   ├── model/          # DTOs de resposta
│   │   │   │   │   └── openapi/        # Documentação OpenAPI
│   │   │   ├── core/                   # Configurações (Security, Email, AWS, etc.)
│   │   │   ├── domain/                 # Entidades, repositórios, serviços de domínio
│   │   │   │   ├── model/              # Entidades JPA
│   │   │   │   ├── repository/         # Repositórios
│   │   │   │   ├── service/            # Serviços de domínio
│   │   │   │   └── exception/          # Exceções personalizadas
│   │   │   └── infrastructure/         # Implementações de infraestrutura
│   │   └── resources/
│   │       ├── db/migration/           # Scripts SQL (Flyway)
│   │       ├── templates/              # Templates de email e páginas
│   │       └── relatorios/             # Relatórios JasperReports
│   └── test/                           # Testes automatizados
├── terraform/                          # Infraestrutura como código (AWS)
├── nginx/                              # Configuração do Nginx
├── docker-compose.yml                  # Orquestração Docker
└── pom.xml                             # Dependências Maven
```

## ⚙️ Pré-requisitos

- **Java 21** ou superior
- **Maven 3.8+**
- **Docker** e **Docker Compose**
- **MySQL 8.0** (ou usar via Docker)
- **Redis** (ou usar via Docker)

## 🔧 Instalação e Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/algafood.git
cd algafood
```

### 2. Configure as variáveis de ambiente

Copie o arquivo de exemplo e configure suas propriedades:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### 3. Inicie os serviços com Docker Compose

```bash
docker-compose up -d
```

Isso iniciará:
- **MySQL** na porta `13306`
- **Redis** na porta `6379`
- **LocalStack** (AWS local) na porta `4566`

### 4. Configure o LocalStack (AWS local)

Execute o script de configuração:

**Windows (PowerShell):**
```powershell
.\setup-localstack.ps1
```

**Linux/Mac:**
```bash
./init-localstack.sh
```

### 5. Execute a aplicação

**Via Maven:**
```bash
./mvnw spring-boot:run
```

**Via IDE:**
Execute a classe principal `AlgafoodApplication.java`

### 6. Acesse a aplicação

- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Documentação OpenAPI:** http://localhost:8080/v3/api-docs

## 🐳 Executando com Docker

Para executar toda a aplicação em containers:

```bash
# Build da imagem
docker build -t algafood-api .

# Execute todos os serviços
docker-compose up -d
```

A API estará disponível em http://localhost:80 (via Nginx)

## 🔐 Autenticação

A API utiliza **OAuth2** com **Authorization Server** integrado. 

### Fluxo de Autenticação

1. Obtenha um token de acesso:
```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=algafood-backend" \
  -d "client_secret=backend123"
```

2. Use o token nas requisições:
```bash
curl -X GET http://localhost:8080/v1/restaurantes \
  -H "Authorization: Bearer {seu_token}"
```

### Clientes OAuth2 Pré-configurados

| Client ID | Client Secret | Uso |
|-----------|---------------|-----|
| `algafood-backend` | `backend123` | Backend/APIs |
| `algafood-web` | `web123` | Aplicações Web |

## 📡 Endpoints da API

A API está versionada em `/v1/`. Principais recursos:

### Restaurantes
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/v1/restaurantes` | Lista todos os restaurantes |
| GET | `/v1/restaurantes/{id}` | Busca restaurante por ID |
| POST | `/v1/restaurantes` | Adiciona novo restaurante |
| PUT | `/v1/restaurantes/{id}` | Atualiza restaurante |
| PUT | `/v1/restaurantes/{id}/ativo` | Ativa restaurante |
| DELETE | `/v1/restaurantes/{id}/ativo` | Inativa restaurante |
| PUT | `/v1/restaurantes/{id}/abertura` | Abre restaurante |
| PUT | `/v1/restaurantes/{id}/fechamento` | Fecha restaurante |

### Pedidos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/v1/pedidos` | Lista pedidos com filtros |
| GET | `/v1/pedidos/{codigo}` | Busca pedido por código |
| POST | `/v1/pedidos` | Cria novo pedido |
| PUT | `/v1/pedidos/{codigo}/confirmacao` | Confirma pedido |
| PUT | `/v1/pedidos/{codigo}/entrega` | Marca como entregue |
| PUT | `/v1/pedidos/{codigo}/cancelamento` | Cancela pedido |

### Produtos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/v1/restaurantes/{id}/produtos` | Lista produtos do restaurante |
| GET | `/v1/restaurantes/{id}/produtos/{produtoId}` | Busca produto |
| POST | `/v1/restaurantes/{id}/produtos` | Adiciona produto |
| PUT | `/v1/restaurantes/{id}/produtos/{produtoId}` | Atualiza produto |

### Cozinhas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/v1/cozinhas` | Lista cozinhas (paginado) |
| GET | `/v1/cozinhas/{id}` | Busca cozinha por ID |
| POST | `/v1/cozinhas` | Adiciona cozinha |
| PUT | `/v1/cozinhas/{id}` | Atualiza cozinha |
| DELETE | `/v1/cozinhas/{id}` | Remove cozinha |

### Cidades
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/v1/cidades` | Lista todas as cidades |
| GET | `/v1/cidades/{id}` | Busca cidade por ID |
| POST | `/v1/cidades` | Adiciona cidade |
| PUT | `/v1/cidades/{id}` | Atualiza cidade |
| DELETE | `/v1/cidades/{id}` | Remove cidade |

### Estados
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/v1/estados` | Lista todos os estados |
| GET | `/v1/estados/{id}` | Busca estado por ID |
| POST | `/v1/estados` | Adiciona estado |
| PUT | `/v1/estados/{id}` | Atualiza estado |
| DELETE | `/v1/estados/{id}` | Remove estado |

### Usuários
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/v1/usuarios` | Lista todos os usuários |
| GET | `/v1/usuarios/{id}` | Busca usuário por ID |
| POST | `/v1/usuarios` | Cadastra novo usuário |
| PUT | `/v1/usuarios/{id}` | Atualiza usuário |
| PUT | `/v1/usuarios/{id}/senha` | Altera senha |

### Formas de Pagamento
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/v1/formas-pagamento` | Lista formas de pagamento |
| GET | `/v1/formas-pagamento/{id}` | Busca por ID |
| POST | `/v1/formas-pagamento` | Adiciona forma de pagamento |
| PUT | `/v1/formas-pagamento/{id}` | Atualiza |
| DELETE | `/v1/formas-pagamento/{id}` | Remove |

### Grupos e Permissões
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/v1/grupos` | Lista grupos |
| GET | `/v1/grupos/{id}/permissoes` | Lista permissões do grupo |
| PUT | `/v1/grupos/{id}/permissoes/{permissaoId}` | Associa permissão |
| DELETE | `/v1/grupos/{id}/permissoes/{permissaoId}` | Desassocia permissão |

### Estatísticas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/v1/estatisticas/vendas-diarias` | Consulta vendas diárias (JSON) |
| GET | `/v1/estatisticas/vendas-diarias` | Relatório vendas diárias (PDF) |

> 📖 **Documentação completa:** Acesse o Swagger UI em `/swagger-ui.html`

## ☁️ Integração AWS

O projeto utiliza os seguintes serviços AWS (emulados localmente via LocalStack):

| Serviço | Uso |
|---------|-----|
| **S3** | Armazenamento de fotos de produtos |
| **SQS** | Fila de eventos de status de pedidos |
| **EventBridge** | Barramento de eventos (pedidos) |
| **SES** | Envio de e-mails transacionais |

### Configuração LocalStack

A infraestrutura AWS local é configurada via Terraform. Para aplicar:

```bash
cd terraform
terraform init
terraform apply
```

Ou use o script:
```bash
.\apply-terraform.bat
```

## 📧 Sistema de E-mails

A API envia e-mails transacionais para:
- Confirmação de pedidos
- Cancelamento de pedidos
- Notificações para restaurantes

Templates de e-mail estão em: `src/main/resources/templates/emails/`

## 📊 Relatórios

Relatórios PDF são gerados com JasperReports:
- **Vendas Diárias:** `/v1/estatisticas/vendas-diarias` (Accept: application/pdf)

## 🧪 Testes

Execute os testes com:

```bash
# Testes unitários e de integração
./mvnw test

# Testes de integração completos
./mvnw verify
```

## 📝 Collection Postman

Uma collection do Postman está disponível em:
`Algafood.postman_collection.json`

Importe no Postman para testar todos os endpoints.

## 🔄 Migrations

As migrations do banco de dados são gerenciadas pelo Flyway e estão em:
`src/main/resources/db/migration/`

Para executar manualmente:
```bash
./mvnw flyway:migrate
```

## 📚 Documentação Adicional

- [Documentação LocalStack](LOCALSTACK-README.md)
- [Swagger UI](http://localhost:8080/swagger-ui.html)
- [OpenAPI JSON](http://localhost:8080/v3/api-docs)

## 🔑 Variáveis de Ambiente (Produção)

Para o ambiente de produção, configure as seguintes variáveis de ambiente:

### Database
| Variável | Descrição | Obrigatória |
|----------|-----------|-------------|
| `DB_HOST` | Host do banco de dados MySQL | Sim |
| `DB_USERNAME` | Usuário do banco de dados | Sim |
| `DB_PASSWORD` | Senha do banco de dados | Sim |

### Redis
| Variável | Descrição | Obrigatória |
|----------|-----------|-------------|
| `REDIS_HOST` | Host do Redis | Sim |
| `REDIS_PORT` | Porta do Redis (padrão: 6379) | Não |
| `REDIS_PASSWORD` | Senha do Redis | Não |

### Security / OAuth2
| Variável | Descrição | Obrigatória |
|----------|-----------|-------------|
| `JWT_KEYSTORE_JKS_LOCATION` | Localização do keystore JKS (base64) | Sim |
| `JWT_KEYSTORE_PASSWORD` | Senha do keystore | Sim |
| `JWT_KEYSTORE_KEYPAIR_ALIAS` | Alias do keypair (padrão: algafood) | Não |
| `AUTH_PROVIDER_URL` | URL do provedor de autenticação | Não |
| `OAUTH_CLIENT_ID` | Client ID OAuth (padrão: algafood-backend) | Não |
| `OAUTH_CLIENT_SECRET` | Client Secret OAuth | Sim |

### Swagger
| Variável | Descrição | Obrigatória |
|----------|-----------|-------------|
| `SWAGGER_OAUTH_CLIENT_ID` | Client ID para Swagger (padrão: algafood-web) | Não |
| `SWAGGER_OAUTH_CLIENT_SECRET` | Client Secret para Swagger | Não |

### AWS
| Variável | Descrição | Obrigatória |
|----------|-----------|-------------|
| `AWS_ACCESS_KEY_ID` | AWS Access Key ID | Sim |
| `AWS_SECRET_ACCESS_KEY` | AWS Secret Access Key | Sim |
| `AWS_REGION` | Região AWS (padrão: us-east-1) | Não |
| `AWS_ENDPOINT_URL` | Endpoint customizado (LocalStack) | Não |
| `S3_BUCKET` | Nome do bucket S3 | Sim |
| `S3_DIRETORIO_FOTOS` | Diretório de fotos no S3 (padrão: catalogo) | Não |
| `SQS_QUEUE_PEDIDO_STATUS` | Nome da fila SQS | Não |
| `EVENTBRIDGE_BUS` | Nome do barramento EventBridge | Não |

### Email
| Variável | Descrição | Obrigatória |
|----------|-----------|-------------|
| `EMAIL_REMETENTE` | E-mail do remetente | Sim |
| `EMAIL_SANDBOX_DESTINATARIO` | E-mail para sandbox (testes) | Não |

### Logging
| Variável | Descrição | Obrigatória |
|----------|-----------|-------------|
| `LOGGLY_TOKEN` | Token do Loggly para logs | Não |

### Exemplo de configuração (.env)

```env
# Database
DB_HOST=mysql.algafood.com.br
DB_USERNAME=algafood
DB_PASSWORD=sua_senha_segura

# Redis
REDIS_HOST=redis.algafood.com.br
REDIS_PORT=6379

# Security
JWT_KEYSTORE_JKS_LOCATION=base64:...
JWT_KEYSTORE_PASSWORD=sua_senha_keystore
OAUTH_CLIENT_SECRET=seu_client_secret

# AWS
AWS_ACCESS_KEY_ID=AKIA...
AWS_SECRET_ACCESS_KEY=sua_secret_key
AWS_REGION=us-east-1
S3_BUCKET=algafood-catalogo

# Email
EMAIL_REMETENTE=naoresponder@algafood.com.br
```

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👨‍💻 Autor

Desenvolvido como parte do curso de Especialista Spring REST da AlgaWorks.

---

⭐ Se este projeto foi útil para você, considere dar uma estrela!

