package com.algaworks.algafood.infrastructure.service.event;

import com.algaworks.algafood.domain.event.PedidoStatusAlteradoEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;

@Slf4j
@Component
public class EventBridgePublisher {

    @Autowired
    private EventBridgeClient eventBridgeClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${algafood.eventbridge.bus}")
    private String eventBusName;

    public void publicarEventoPedido(PedidoStatusAlteradoEvent evento) {
        try {
            String jsonEvento = objectMapper.writeValueAsString(evento);

            PutEventsRequestEntry requestEntry = PutEventsRequestEntry.builder()
                    .source("com.algaworks.algafood")
                    .detailType("PedidoStatusAlterado")
                    .detail(jsonEvento)
                    .eventBusName(eventBusName)
                    .build();

            PutEventsRequest request = PutEventsRequest.builder()
                    .entries(requestEntry)
                    .build();

            eventBridgeClient.putEvents(request);
            
            log.info("Evento de status do pedido publicado no EventBridge: {}", evento.getCodigoPedido());

        } catch (JsonProcessingException e) {
            log.error("Erro ao converter evento para JSON", e);
        } catch (Exception e) {
            log.error("Erro ao publicar evento no EventBridge", e);
        }
    }
}
