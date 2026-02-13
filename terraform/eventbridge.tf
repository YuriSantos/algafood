resource "aws_cloudwatch_event_bus" "algafood_bus" {
  name = "algafood-event-bus"
}

resource "aws_cloudwatch_event_rule" "pedido_status_rule" {
  name           = "algafood-pedido-status-rule"
  description    = "Captura eventos de alteração de status de pedidos"
  event_bus_name = aws_cloudwatch_event_bus.algafood_bus.name

  event_pattern = jsonencode({
    "source" : ["com.algaworks.algafood"],
    "detail-type" : ["PedidoStatusAlterado"]
  })
}

resource "aws_cloudwatch_event_target" "sqs_target" {
  rule           = aws_cloudwatch_event_rule.pedido_status_rule.name
  event_bus_name = aws_cloudwatch_event_bus.algafood_bus.name
  target_id      = "SQS"
  arn            = aws_sqs_queue.pedido_status_queue.arn

  # Input Transformer para enviar apenas o conteúdo do detail
  input_transformer {
    input_paths = {
      detail = "$.detail"
    }
    input_template = "<detail>"
  }
}

resource "aws_sqs_queue_policy" "allow_eventbridge_to_send_messages" {
  queue_url = aws_sqs_queue.pedido_status_queue.id

  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Principal" : "*",
        "Action" : "sqs:SendMessage",
        "Resource" : aws_sqs_queue.pedido_status_queue.arn
      }
    ]
  })
}
