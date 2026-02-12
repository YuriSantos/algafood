package com.algaworks.algafood.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Wrapper para eventos recebidos do EventBridge via SQS.
 * O EventBridge envelopa o evento original em uma estrutura padrão.
 *
 * Estrutura da mensagem:
 * {
 *   "version": "0",
 *   "id": "uuid",
 *   "detail-type": "PedidoStatusAlterado",
 *   "source": "com.algaworks.algafood",
 *   "account": "000000000000",
 *   "time": "2026-02-12T10:00:00Z",
 *   "region": "us-east-1",
 *   "resources": [],
 *   "detail": {
 *     "codigoPedido": "abc123",
 *     "novoStatus": "CONFIRMADO",
 *     "restauranteId": 1,
 *     "clienteId": 1
 *   }
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventBridgeEnvelope<T> {

    private String version;

    private String id;

    @JsonProperty("detail-type")
    private String detailType;

    private String source;

    private String account;

    private OffsetDateTime time;

    private String region;

    private List<String> resources;

    private T detail;
}

