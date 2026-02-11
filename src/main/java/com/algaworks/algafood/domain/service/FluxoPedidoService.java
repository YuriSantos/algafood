package com.algaworks.algafood.domain.service;

import com.algaworks.algafood.domain.event.PedidoStatusAlteradoEvent;
import com.algaworks.algafood.infrastructure.service.event.EventBridgePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.model.Pedido;
import com.algaworks.algafood.domain.repository.PedidoRepository;

@Slf4j
@Service
public class FluxoPedidoService {

	@Autowired
	private EmissaoPedidoService emissaoPedido;
	
	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private EventBridgePublisher eventBridgePublisher;
	
	@Transactional
	public void confirmar(String codigoPedido) {
		log.info("Confirmando pedido com código: {}", codigoPedido);
		Pedido pedido = emissaoPedido.buscarOuFalhar(codigoPedido);
		pedido.confirmar();
		
		pedidoRepository.save(pedido);
		eventBridgePublisher.publicarEventoPedido(new PedidoStatusAlteradoEvent(pedido));
	}
	
	@Transactional
	public void cancelar(String codigoPedido) {
		log.info("Cancelando pedido com código: {}", codigoPedido);
		Pedido pedido = emissaoPedido.buscarOuFalhar(codigoPedido);
		pedido.cancelar();
		
		pedidoRepository.save(pedido);
		eventBridgePublisher.publicarEventoPedido(new PedidoStatusAlteradoEvent(pedido));
	}
	
	@Transactional
	public void entregar(String codigoPedido) {
		log.info("Entregando pedido com código: {}", codigoPedido);
		Pedido pedido = emissaoPedido.buscarOuFalhar(codigoPedido);
		pedido.entregar();
		
		// O método entregar() já salva o pedido se ele usar o padrão de repositório do DDD corretamente,
		// mas aqui no código original não tinha o save explícito.
		// Assumindo que o método entregar altera o estado e o save é necessário para persistir ou disparar eventos do JPA.
		// Se o método entregar() for transacional e o objeto estiver gerenciado, o JPA salva sozinho.
		// Mas para garantir, vou manter como estava, apenas adicionando o evento.
		
		eventBridgePublisher.publicarEventoPedido(new PedidoStatusAlteradoEvent(pedido));
	}
	
}
