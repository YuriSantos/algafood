package com.algaworks.algafood.domain.event;

import com.algaworks.algafood.domain.model.Pedido;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoStatusAlteradoEvent {

    private String codigoPedido;
    private String novoStatus;
    private Long restauranteId;
    private Long clienteId;

    public PedidoStatusAlteradoEvent(Pedido pedido) {
        this.codigoPedido = pedido.getCodigo();
        this.novoStatus = pedido.getStatus().name();
        this.restauranteId = pedido.getRestaurante().getId();
        this.clienteId = pedido.getCliente().getId();
    }
}
