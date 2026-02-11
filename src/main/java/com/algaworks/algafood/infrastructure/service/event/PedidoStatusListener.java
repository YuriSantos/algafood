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
        log.info("Recebido evento de alteração de status do pedido via SQS: {}", evento.getCodigoPedido());
        log.info("Novo status: {}", evento.getNovoStatus());
        
        // Aqui você implementaria a lógica para reagir ao evento
        // Por exemplo, enviar um email, notificar outro sistema, etc.
    }
}
