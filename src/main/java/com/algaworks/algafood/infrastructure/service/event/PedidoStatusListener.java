package com.algaworks.algafood.infrastructure.service.event;

import com.algaworks.algafood.domain.event.PedidoStatusAlteradoEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PedidoStatusListener {

    @SqsListener("${algafood.sqs.queue.pedido-status}")
    public void escutarEventoPedido(PedidoStatusAlteradoEvent evento) {
        try {
            log.info("Iniciando processamento de evento SQS para o pedido: {}", evento.getCodigoPedido());
            log.debug("Detalhes do evento: Novo Status={}, RestauranteId={}, ClienteId={}", 
                    evento.getNovoStatus(), evento.getRestauranteId(), evento.getClienteId());
            
            // Simulação de processamento
            // Aqui você implementaria a lógica real, como enviar notificações, atualizar dashboards, etc.
            
            log.info("Evento do pedido {} processado com sucesso.", evento.getCodigoPedido());
        } catch (Exception e) {
            log.error("Erro ao processar evento SQS para o pedido: {}", evento.getCodigoPedido(), e);
            // Lançar a exceção é importante para que o SQS saiba que a mensagem não foi processada
            // e possa tentar entregá-la novamente (DLQ, retries, etc.)
            throw e;
        }
    }
}
