# Como Rodar AWS Localmente com LocalStack

Este guia explica como configurar e rodar os serviços AWS (SQS, EventBridge, SES) localmente usando o **LocalStack**.

## Pré-requisitos

- **Docker** instalado e rodando
- **Docker Compose** (já incluído no Docker Desktop)

## Passo a Passo

### 1. Iniciar o LocalStack

```powershell
# Navegue até a pasta do projeto
cd C:\Users\yuris\IdeaProjects\algafood

# Inicie o LocalStack via Docker Compose
docker-compose up -d localstack
```

Ou inicie diretamente um container existente:
```powershell
docker start algafood-localstack-1
```

### 2. Verificar se o LocalStack está rodando

```powershell
docker ps
```

Você deve ver um container `localstack/localstack:3.0` rodando na porta `4566`.

### 3. Configurar os Recursos AWS

Copie e execute o script de inicialização:

```powershell
# Copiar o script para o container
docker cp init-localstack.sh algafood-localstack-1:/tmp/init-localstack.sh

# Executar o script
docker exec algafood-localstack-1 bash /tmp/init-localstack.sh
```

Isso criará:
- **Fila SQS**: `algafood-pedido-status`
- **Event Bus**: `algafood-event-bus`
- **Regra EventBridge**: `algafood-pedido-status-rule`
- **Identidade SES**: `teste@algafood.com.br`

### 4. Verificar os Recursos Criados

```powershell
# Listar filas SQS
docker exec algafood-localstack-1 awslocal sqs list-queues

# Listar Event Buses
docker exec algafood-localstack-1 awslocal events list-event-buses

# Listar regras do EventBridge
docker exec algafood-localstack-1 awslocal events list-rules --event-bus-name algafood-event-bus

# Listar identidades SES
docker exec algafood-localstack-1 awslocal ses list-identities
```

### 5. Iniciar o MySQL e Redis (se necessário)

```powershell
docker-compose up -d algafood-mysql algafood-redis
```

### 6. Rodar a Aplicação

```powershell
# Configure o JAVA_HOME (se necessário)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"

# Execute a aplicação
.\mvnw.cmd spring-boot:run
```

## Configuração da Aplicação

A aplicação já está configurada para usar o LocalStack no arquivo `application.properties`:

```properties
# AWS Configuration (LocalStack)
spring.cloud.aws.endpoint=${AWS_ENDPOINT_URL:http://localhost:4566}
spring.cloud.aws.credentials.access-key=test
spring.cloud.aws.credentials.secret-key=test
spring.cloud.aws.region.static=us-east-1
spring.cloud.aws.sqs.enabled=true

# SQS Queue
algafood.sqs.queue.pedido-status=algafood-pedido-status

# EventBridge
algafood.eventbridge.bus=algafood-event-bus
```

## Fluxo de Eventos

1. **Mudança de Status do Pedido** → Evento publicado no **EventBridge**
2. **EventBridge** roteia para a **Fila SQS**
3. **SqsListener** consome a mensagem
4. **Email** é enviado para o cliente via **SES**

## Comandos Úteis

### Enviar uma mensagem de teste para a fila SQS
```powershell
docker exec algafood-localstack-1 awslocal sqs send-message `
    --queue-url http://localhost:4566/000000000000/algafood-pedido-status `
    --message-body '{"codigoPedido":"abc123","novoStatus":"CONFIRMADO","restauranteId":1,"clienteId":1}'
```

### Ver mensagens na fila
```powershell
docker exec algafood-localstack-1 awslocal sqs receive-message `
    --queue-url http://localhost:4566/000000000000/algafood-pedido-status
```

### Publicar evento no EventBridge
```powershell
docker exec algafood-localstack-1 awslocal events put-events `
    --entries '[{"Source":"com.algaworks.algafood","DetailType":"PedidoStatusAlterado","Detail":"{\"codigoPedido\":\"abc123\",\"novoStatus\":\"CONFIRMADO\",\"restauranteId\":1,\"clienteId\":1}","EventBusName":"algafood-event-bus"}]'
```

### Ver logs do LocalStack
```powershell
docker logs algafood-localstack-1 -f
```

## Troubleshooting

### Container não inicia
```powershell
# Remover container existente
docker rm algafood-localstack-1

# Recriar via docker-compose
docker-compose up -d localstack
```

### Recursos não foram criados
```powershell
# Re-executar o script de inicialização
docker exec algafood-localstack-1 bash /tmp/init-localstack.sh
```

### Verificar saúde do LocalStack
```powershell
docker exec algafood-localstack-1 curl -s http://localhost:4566/_localstack/health
```

## Arquitetura

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────┐
│  FluxoPedido    │───▶│   EventBridge    │───▶│  SQS Queue  │
│    Service      │    │  (Event Bus)     │    │             │
└─────────────────┘    └──────────────────┘    └──────┬──────┘
                                                      │
                                                      ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────┐
│     Email       │◀───│  PedidoStatus    │◀───│ SqsListener │
│     (SES)       │    │    Listener      │    │             │
└─────────────────┘    └──────────────────┘    └─────────────┘
```

