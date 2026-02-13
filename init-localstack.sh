#!/bin/bash

# Script para configurar os recursos AWS no LocalStack
echo "========================================="
echo " Configurando recursos no LocalStack..."
echo "========================================="

# Criar DLQ (Dead Letter Queue)
echo "[1/6] Criando DLQ (Dead Letter Queue)..."
awslocal sqs create-queue --queue-name algafood-pedido-status-dlq --attributes MessageRetentionPeriod=1209600 2>/dev/null || echo "DLQ já existe"
echo "DLQ OK!"

# Obter ARN da DLQ
DLQ_ARN=$(awslocal sqs get-queue-attributes --queue-url http://localhost:4566/000000000000/algafood-pedido-status-dlq --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)
echo "DLQ ARN: $DLQ_ARN"

# Criar fila principal com redrive policy para DLQ
echo "[2/6] Criando fila SQS principal com redrive policy..."
awslocal sqs create-queue \
    --queue-name algafood-pedido-status \
    --attributes "{\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"$DLQ_ARN\\\",\\\"maxReceiveCount\\\":\\\"3\\\"}\"}" 2>/dev/null || echo "Fila já existe"
echo "Fila SQS OK!"

# Obter ARN da fila
QUEUE_ARN=$(awslocal sqs get-queue-attributes --queue-url http://localhost:4566/000000000000/algafood-pedido-status --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)
echo "Queue ARN: $QUEUE_ARN"

# Criar Event Bus
echo "[3/6] Criando Event Bus..."
awslocal events create-event-bus --name algafood-event-bus 2>/dev/null || echo "Event Bus já existe"
echo "Event Bus OK!"

# Criar regra no EventBridge
echo "[4/6] Criando regra no EventBridge..."
awslocal events put-rule \
    --name algafood-pedido-status-rule \
    --event-bus-name algafood-event-bus \
    --event-pattern '{
        "source": ["com.algaworks.algafood"],
        "detail-type": ["PedidoStatusAlterado"]
    }'
echo "Regra criada!"

# Associar SQS como target com Input Transformer (envia apenas o detail)
echo "[5/6] Associando SQS como target com Input Transformer..."
awslocal events put-targets \
    --rule algafood-pedido-status-rule \
    --event-bus-name algafood-event-bus \
    --targets "[{
        \"Id\": \"SQS\",
        \"Arn\": \"$QUEUE_ARN\",
        \"InputTransformer\": {
            \"InputPathsMap\": {
                \"detail\": \"$.detail\"
            },
            \"InputTemplate\": \"<detail>\"
        }
    }]"
echo "Target associado com Input Transformer!"

# Verificar email identity (SES)
echo "[6/6] Verificando email identity no SES..."
awslocal ses verify-email-identity --email-address teste@algafood.com.br
echo "Email verificado!"

echo ""
echo "========================================="
echo " Recursos configurados com sucesso!"
echo "========================================="
echo ""
echo "Filas SQS:"
echo "  - Principal: algafood-pedido-status"
echo "  - DLQ: algafood-pedido-status-dlq (max 3 tentativas)"
echo ""
echo "EventBridge:"
echo "  - Event Bus: algafood-event-bus"
echo "  - Regra: algafood-pedido-status-rule"
echo ""
echo "SES:"
echo "  - Email verificado: teste@algafood.com.br"
echo ""
