# AlgaFood API - Documentação Técnica

## Visão Geral

A AlgaFood API é uma API RESTful desenvolvida para gerenciamento de um sistema de delivery de comida. Esta documentação detalha os recursos disponíveis, modelos de dados e exemplos de uso.

## Base URL

```
http://localhost:8080/v1
```

Em produção, substitua pelo domínio apropriado.

---

## Autenticação

A API utiliza **OAuth 2.0** para autenticação. Todas as requisições (exceto algumas públicas) requerem um token de acesso válido.

### Obtendo um Token

**Endpoint:** `POST /oauth2/token`

**Headers:**
```
Content-Type: application/x-www-form-urlencoded
```

**Body (Client Credentials):**
```
grant_type=client_credentials
client_id=algafood-backend
client_secret=backend123
```

**Body (Password Grant - se habilitado):**
```
grant_type=password
username=admin@algafood.com
password=123
client_id=algafood-web
client_secret=web123
```

**Resposta:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "READ WRITE"
}
```

### Usando o Token

Inclua o token no header `Authorization`:

```
Authorization: Bearer {access_token}
```

---

## Códigos de Status HTTP

| Código | Descrição |
|--------|-----------|
| `200` | OK - Requisição bem-sucedida |
| `201` | Created - Recurso criado com sucesso |
| `204` | No Content - Requisição bem-sucedida sem conteúdo |
| `400` | Bad Request - Dados inválidos na requisição |
| `401` | Unauthorized - Token inválido ou ausente |
| `403` | Forbidden - Sem permissão para o recurso |
| `404` | Not Found - Recurso não encontrado |
| `409` | Conflict - Conflito de dados |
| `500` | Internal Server Error - Erro interno do servidor |

---

## Formato de Erro

Todas as respostas de erro seguem o padrão RFC 7807 (Problem Details):

```json
{
  "type": "https://algafood.com.br/entidade-nao-encontrada",
  "title": "Entidade não encontrada",
  "status": 404,
  "detail": "Não existe um cadastro de restaurante com código 999",
  "timestamp": "2024-01-15T10:30:00Z",
  "userMessage": "Não existe um cadastro de restaurante com código 999"
}
```

---

## Recursos da API

### 1. Restaurantes

Gerenciamento de restaurantes cadastrados na plataforma.

#### Listar Restaurantes

```http
GET /v1/restaurantes
```

**Parâmetros de Query:**
- `projecao=apenas-nome` - Retorna apenas ID e nome

**Resposta (200):**
```json
{
  "_embedded": {
    "restaurantes": [
      {
        "id": 1,
        "nome": "Thai Gourmet",
        "taxaFrete": 10.00,
        "ativo": true,
        "aberto": true,
        "cozinha": {
          "id": 1,
          "nome": "Tailandesa"
        },
        "_links": {
          "self": { "href": "http://localhost:8080/v1/restaurantes/1" },
          "restaurantes": { "href": "http://localhost:8080/v1/restaurantes" }
        }
      }
    ]
  },
  "_links": {
    "self": { "href": "http://localhost:8080/v1/restaurantes" }
  }
}
```

#### Buscar Restaurante

```http
GET /v1/restaurantes/{restauranteId}
```

**Resposta (200):**
```json
{
  "id": 1,
  "nome": "Thai Gourmet",
  "taxaFrete": 10.00,
  "ativo": true,
  "aberto": true,
  "dataCadastro": "2024-01-15T10:00:00Z",
  "dataAtualizacao": "2024-01-15T12:00:00Z",
  "cozinha": {
    "id": 1,
    "nome": "Tailandesa"
  },
  "endereco": {
    "cep": "01310-100",
    "logradouro": "Avenida Paulista",
    "numero": "1000",
    "bairro": "Bela Vista",
    "cidade": {
      "id": 1,
      "nome": "São Paulo",
      "estado": "São Paulo"
    }
  },
  "_links": {
    "self": { "href": "http://localhost:8080/v1/restaurantes/1" },
    "produtos": { "href": "http://localhost:8080/v1/restaurantes/1/produtos" },
    "formas-pagamento": { "href": "http://localhost:8080/v1/restaurantes/1/formas-pagamento" }
  }
}
```

#### Criar Restaurante

```http
POST /v1/restaurantes
```

**Body:**
```json
{
  "nome": "Novo Restaurante",
  "taxaFrete": 15.00,
  "cozinha": {
    "id": 1
  },
  "endereco": {
    "cep": "01310-100",
    "logradouro": "Avenida Paulista",
    "numero": "1500",
    "bairro": "Bela Vista",
    "cidade": {
      "id": 1
    }
  }
}
```

**Resposta (201):**
```json
{
  "id": 5,
  "nome": "Novo Restaurante",
  "taxaFrete": 15.00,
  ...
}
```

#### Atualizar Restaurante

```http
PUT /v1/restaurantes/{restauranteId}
```

**Body:** Mesmo formato do POST

#### Ativar/Inativar Restaurante

```http
PUT /v1/restaurantes/{restauranteId}/ativo
DELETE /v1/restaurantes/{restauranteId}/ativo
```

**Resposta (204):** No Content

#### Abrir/Fechar Restaurante

```http
PUT /v1/restaurantes/{restauranteId}/abertura
PUT /v1/restaurantes/{restauranteId}/fechamento
```

**Resposta (204):** No Content

---

### 2. Pedidos

Gerenciamento de pedidos realizados pelos clientes.

#### Listar Pedidos

```http
GET /v1/pedidos
```

**Parâmetros de Query (filtros):**
- `clienteId` - Filtrar por cliente
- `restauranteId` - Filtrar por restaurante
- `dataCriacaoInicio` - Data início (ISO 8601)
- `dataCriacaoFim` - Data fim (ISO 8601)
- `page` - Número da página (0-based)
- `size` - Tamanho da página (padrão: 10)
- `sort` - Ordenação (ex: `dataCriacao,desc`)

**Resposta (200):**
```json
{
  "_embedded": {
    "pedidos": [
      {
        "codigo": "04813f77-79b5-11ec-9a17-0242ac1b0002",
        "subtotal": 79.00,
        "taxaFrete": 10.00,
        "valorTotal": 89.00,
        "status": "CONFIRMADO",
        "dataCriacao": "2024-01-15T14:30:00Z",
        "restaurante": {
          "id": 1,
          "nome": "Thai Gourmet"
        },
        "cliente": {
          "id": 1,
          "nome": "João Silva"
        }
      }
    ]
  },
  "page": {
    "size": 10,
    "totalElements": 50,
    "totalPages": 5,
    "number": 0
  }
}
```

#### Buscar Pedido

```http
GET /v1/pedidos/{codigoPedido}
```

**Resposta (200):**
```json
{
  "codigo": "04813f77-79b5-11ec-9a17-0242ac1b0002",
  "subtotal": 79.00,
  "taxaFrete": 10.00,
  "valorTotal": 89.00,
  "status": "CONFIRMADO",
  "dataCriacao": "2024-01-15T14:30:00Z",
  "dataConfirmacao": "2024-01-15T14:35:00Z",
  "formaPagamento": {
    "id": 1,
    "descricao": "Cartão de crédito"
  },
  "restaurante": {
    "id": 1,
    "nome": "Thai Gourmet"
  },
  "cliente": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com"
  },
  "enderecoEntrega": {
    "cep": "01310-200",
    "logradouro": "Rua Augusta",
    "numero": "500",
    "bairro": "Consolação",
    "cidade": {
      "id": 1,
      "nome": "São Paulo",
      "estado": "São Paulo"
    }
  },
  "itens": [
    {
      "produtoId": 1,
      "produtoNome": "Pad Thai",
      "quantidade": 2,
      "precoUnitario": 35.00,
      "precoTotal": 70.00,
      "observacao": "Sem amendoim"
    }
  ],
  "_links": {
    "self": { "href": "http://localhost:8080/v1/pedidos/04813f77-79b5-11ec-9a17-0242ac1b0002" },
    "confirmar": { "href": "http://localhost:8080/v1/pedidos/04813f77-79b5-11ec-9a17-0242ac1b0002/confirmacao" },
    "entregar": { "href": "http://localhost:8080/v1/pedidos/04813f77-79b5-11ec-9a17-0242ac1b0002/entrega" },
    "cancelar": { "href": "http://localhost:8080/v1/pedidos/04813f77-79b5-11ec-9a17-0242ac1b0002/cancelamento" }
  }
}
```

#### Criar Pedido

```http
POST /v1/pedidos
```

**Body:**
```json
{
  "restaurante": {
    "id": 1
  },
  "formaPagamento": {
    "id": 1
  },
  "enderecoEntrega": {
    "cep": "01310-200",
    "logradouro": "Rua Augusta",
    "numero": "500",
    "complemento": "Apto 101",
    "bairro": "Consolação",
    "cidade": {
      "id": 1
    }
  },
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2,
      "observacao": "Sem amendoim"
    },
    {
      "produtoId": 3,
      "quantidade": 1
    }
  ]
}
```

**Resposta (201):** Pedido criado com código gerado

#### Fluxo do Pedido

```http
PUT /v1/pedidos/{codigoPedido}/confirmacao   # Confirmar pedido
PUT /v1/pedidos/{codigoPedido}/entrega       # Marcar como entregue
PUT /v1/pedidos/{codigoPedido}/cancelamento  # Cancelar pedido
```

**Resposta (204):** No Content

**Status possíveis do pedido:**
- `CRIADO` - Pedido criado, aguardando confirmação
- `CONFIRMADO` - Pedido confirmado pelo restaurante
- `ENTREGUE` - Pedido entregue ao cliente
- `CANCELADO` - Pedido cancelado

---

### 3. Produtos

Gerenciamento de produtos dos restaurantes.

#### Listar Produtos do Restaurante

```http
GET /v1/restaurantes/{restauranteId}/produtos
```

**Parâmetros de Query:**
- `incluirInativos=true` - Incluir produtos inativos

**Resposta (200):**
```json
{
  "_embedded": {
    "produtos": [
      {
        "id": 1,
        "nome": "Pad Thai",
        "descricao": "Macarrão tailandês com camarão",
        "preco": 35.00,
        "ativo": true,
        "_links": {
          "self": { "href": "http://localhost:8080/v1/restaurantes/1/produtos/1" },
          "foto": { "href": "http://localhost:8080/v1/restaurantes/1/produtos/1/foto" }
        }
      }
    ]
  }
}
```

#### Criar Produto

```http
POST /v1/restaurantes/{restauranteId}/produtos
```

**Body:**
```json
{
  "nome": "Tom Yum",
  "descricao": "Sopa tailandesa picante com camarão",
  "preco": 28.00,
  "ativo": true
}
```

#### Foto do Produto

```http
PUT /v1/restaurantes/{restauranteId}/produtos/{produtoId}/foto
```

**Content-Type:** `multipart/form-data`

**Body:**
- `arquivo` - Arquivo da foto (JPEG/PNG)
- `descricao` - Descrição da foto

```http
GET /v1/restaurantes/{restauranteId}/produtos/{produtoId}/foto
```

Retorna a foto do produto.

---

### 4. Cozinhas

Gerenciamento de tipos de cozinha.

#### Listar Cozinhas

```http
GET /v1/cozinhas
```

**Parâmetros de Query:**
- `page` - Número da página
- `size` - Tamanho da página

**Resposta (200):**
```json
{
  "_embedded": {
    "cozinhas": [
      {
        "id": 1,
        "nome": "Tailandesa",
        "_links": {
          "self": { "href": "http://localhost:8080/v1/cozinhas/1" }
        }
      }
    ]
  },
  "page": {
    "size": 10,
    "totalElements": 5,
    "totalPages": 1,
    "number": 0
  }
}
```

#### CRUD de Cozinha

```http
GET /v1/cozinhas/{cozinhaId}      # Buscar
POST /v1/cozinhas                 # Criar
PUT /v1/cozinhas/{cozinhaId}      # Atualizar
DELETE /v1/cozinhas/{cozinhaId}   # Remover
```

---

### 5. Cidades

Gerenciamento de cidades.

#### Listar Cidades

```http
GET /v1/cidades
```

**Resposta (200):**
```json
{
  "_embedded": {
    "cidades": [
      {
        "id": 1,
        "nome": "São Paulo",
        "estado": {
          "id": 1,
          "nome": "São Paulo"
        }
      }
    ]
  }
}
```

#### CRUD de Cidade

```http
GET /v1/cidades/{cidadeId}
POST /v1/cidades
PUT /v1/cidades/{cidadeId}
DELETE /v1/cidades/{cidadeId}
```

**Body (POST/PUT):**
```json
{
  "nome": "Campinas",
  "estado": {
    "id": 1
  }
}
```

---

### 6. Estados

Gerenciamento de estados.

```http
GET /v1/estados                  # Listar
GET /v1/estados/{estadoId}       # Buscar
POST /v1/estados                 # Criar
PUT /v1/estados/{estadoId}       # Atualizar
DELETE /v1/estados/{estadoId}    # Remover
```

**Body (POST/PUT):**
```json
{
  "nome": "São Paulo"
}
```

---

### 7. Usuários

Gerenciamento de usuários do sistema.

#### Listar Usuários

```http
GET /v1/usuarios
```

#### CRUD de Usuário

```http
GET /v1/usuarios/{usuarioId}
POST /v1/usuarios
PUT /v1/usuarios/{usuarioId}
```

**Body (POST):**
```json
{
  "nome": "Maria Santos",
  "email": "maria@email.com",
  "senha": "123456"
}
```

**Body (PUT):**
```json
{
  "nome": "Maria Santos Silva",
  "email": "maria.silva@email.com"
}
```

#### Alterar Senha

```http
PUT /v1/usuarios/{usuarioId}/senha
```

**Body:**
```json
{
  "senhaAtual": "123456",
  "novaSenha": "novaSenha123"
}
```

#### Grupos do Usuário

```http
GET /v1/usuarios/{usuarioId}/grupos
PUT /v1/usuarios/{usuarioId}/grupos/{grupoId}
DELETE /v1/usuarios/{usuarioId}/grupos/{grupoId}
```

---

### 8. Grupos e Permissões

#### Grupos

```http
GET /v1/grupos
GET /v1/grupos/{grupoId}
POST /v1/grupos
PUT /v1/grupos/{grupoId}
DELETE /v1/grupos/{grupoId}
```

#### Permissões do Grupo

```http
GET /v1/grupos/{grupoId}/permissoes
PUT /v1/grupos/{grupoId}/permissoes/{permissaoId}
DELETE /v1/grupos/{grupoId}/permissoes/{permissaoId}
```

#### Listar Permissões

```http
GET /v1/permissoes
```

---

### 9. Formas de Pagamento

```http
GET /v1/formas-pagamento
GET /v1/formas-pagamento/{formaPagamentoId}
POST /v1/formas-pagamento
PUT /v1/formas-pagamento/{formaPagamentoId}
DELETE /v1/formas-pagamento/{formaPagamentoId}
```

**Body (POST/PUT):**
```json
{
  "descricao": "PIX"
}
```

#### Formas de Pagamento do Restaurante

```http
GET /v1/restaurantes/{restauranteId}/formas-pagamento
PUT /v1/restaurantes/{restauranteId}/formas-pagamento/{formaPagamentoId}
DELETE /v1/restaurantes/{restauranteId}/formas-pagamento/{formaPagamentoId}
```

---

### 10. Estatísticas

#### Vendas Diárias

```http
GET /v1/estatisticas/vendas-diarias
```

**Parâmetros de Query:**
- `restauranteId` - Filtrar por restaurante
- `dataCriacaoInicio` - Data início
- `dataCriacaoFim` - Data fim
- `timeOffset` - Offset de timezone (ex: `-03:00`)

**Accept:** `application/json` ou `application/pdf`

**Resposta JSON (200):**
```json
[
  {
    "data": "2024-01-15",
    "totalVendas": 25,
    "totalFaturado": 2500.00
  },
  {
    "data": "2024-01-16",
    "totalVendas": 30,
    "totalFaturado": 3200.00
  }
]
```

**Resposta PDF:**
Arquivo PDF com relatório de vendas diárias.

---

## HATEOAS

A API implementa HATEOAS (Hypermedia as the Engine of Application State). Cada recurso inclui links para ações e recursos relacionados.

Exemplo de links em um restaurante:
```json
"_links": {
  "self": { "href": "http://localhost:8080/v1/restaurantes/1" },
  "restaurantes": { "href": "http://localhost:8080/v1/restaurantes" },
  "produtos": { "href": "http://localhost:8080/v1/restaurantes/1/produtos" },
  "formas-pagamento": { "href": "http://localhost:8080/v1/restaurantes/1/formas-pagamento" },
  "responsaveis": { "href": "http://localhost:8080/v1/restaurantes/1/responsaveis" },
  "ativar": { "href": "http://localhost:8080/v1/restaurantes/1/ativo" },
  "abrir": { "href": "http://localhost:8080/v1/restaurantes/1/abertura" }
}
```

---

## Paginação

Recursos que retornam listas suportam paginação:

**Parâmetros:**
- `page` - Número da página (0-based)
- `size` - Itens por página
- `sort` - Campo e direção (ex: `nome,asc`)

**Resposta:**
```json
{
  "_embedded": { ... },
  "_links": {
    "first": { "href": "...?page=0&size=10" },
    "prev": { "href": "...?page=0&size=10" },
    "self": { "href": "...?page=1&size=10" },
    "next": { "href": "...?page=2&size=10" },
    "last": { "href": "...?page=4&size=10" }
  },
  "page": {
    "size": 10,
    "totalElements": 50,
    "totalPages": 5,
    "number": 1
  }
}
```

---

## Cache

Alguns recursos suportam cache HTTP:

**Headers de Cache:**
- `ETag` - Tag de versão do recurso
- `Cache-Control` - Diretivas de cache
- `Last-Modified` - Data da última modificação

**Exemplo:**
```http
GET /v1/formas-pagamento
If-None-Match: "abc123"
```

Se o recurso não mudou, retorna `304 Not Modified`.

---

## Eventos e Mensageria

A API publica eventos em filas SQS quando:
- Pedido é confirmado
- Pedido é cancelado
- Pedido é entregue

**Fila:** `algafood-pedido-status`

**Formato do evento:**
```json
{
  "codigoPedido": "04813f77-79b5-11ec-9a17-0242ac1b0002",
  "statusAnterior": "CRIADO",
  "statusNovo": "CONFIRMADO",
  "dataEvento": "2024-01-15T14:35:00Z"
}
```

---

## Swagger UI

Acesse a documentação interativa em:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:
```
http://localhost:8080/v3/api-docs
```

---

## Suporte

Para dúvidas ou problemas, consulte:
- [README principal](README.md)
- [Documentação LocalStack](LOCALSTACK-README.md)
- Collection Postman: `Algafood.postman_collection.json`

