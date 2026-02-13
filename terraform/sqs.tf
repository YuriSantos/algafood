# Dead Letter Queue para mensagens que falharam
resource "aws_sqs_queue" "pedido_status_dlq" {
  name = "algafood-pedido-status-dlq"

  # Tempo de retenção da mensagem na DLQ (14 dias)
  message_retention_seconds = 1209600
}

# Fila principal com redrive policy para DLQ
resource "aws_sqs_queue" "pedido_status_queue" {
  name = "algafood-pedido-status"

  # Política de redirecionamento para DLQ
  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.pedido_status_dlq.arn
    maxReceiveCount     = 3  # Após 3 tentativas falhas, move para DLQ
  })
}

# Política para permitir que a fila principal envie mensagens para a DLQ
resource "aws_sqs_queue_redrive_allow_policy" "pedido_status_dlq_allow" {
  queue_url = aws_sqs_queue.pedido_status_dlq.id

  redrive_allow_policy = jsonencode({
    redrivePermission = "byQueue"
    sourceQueueArns   = [aws_sqs_queue.pedido_status_queue.arn]
  })
}
