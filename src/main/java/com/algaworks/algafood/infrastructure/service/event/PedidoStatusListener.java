package com.algaworks.algafood.infrastructure.service.event;

import com.algaworks.algafood.domain.event.PedidoStatusAlteradoEvent;
import com.algaworks.algafood.domain.model.Pedido;
import com.algaworks.algafood.domain.model.StatusPedido;
import com.algaworks.algafood.domain.repository.PedidoRepository;
import com.algaworks.algafood.domain.service.EnvioEmailService;
import com.algaworks.algafood.domain.service.EnvioEmailService.Mensagem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoStatusListener {

    private final PedidoRepository pedidoRepository;
    private final EnvioEmailService envioEmailService;
    private final ObjectMapper objectMapper;

    /**
     * Escuta mensagens da fila SQS de status de pedido.
     * Recebe como String para poder logar e tratar diferentes formatos de mensagem.
     */
    @SqsListener("${algafood.sqs.queue.pedido-status}")
    public void escutarEventoPedido(String mensagemJson) {
        try {
            log.info("Mensagem SQS recebida (raw): {}", mensagemJson);

            PedidoStatusAlteradoEvent evento = extrairEvento(mensagemJson);

            if (evento == null || evento.getCodigoPedido() == null) {
                log.error("Evento inválido recebido: codigoPedido é nulo. Mensagem: {}", mensagemJson);
                throw new RuntimeException("Evento inválido recebido da fila: codigoPedido é nulo");
            }

            log.info("Evento extraído: codigoPedido={}, novoStatus={}",
                    evento.getCodigoPedido(), evento.getNovoStatus());

            // Buscar o pedido pelo código
            Pedido pedido = pedidoRepository.findByCodigo(evento.getCodigoPedido())
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + evento.getCodigoPedido()));

            // Enviar email baseado no status
            enviarEmailParaStatus(pedido, StatusPedido.valueOf(evento.getNovoStatus()));

            log.info("Evento do pedido {} processado com sucesso.", evento.getCodigoPedido());
        } catch (Exception e) {
            log.error("Erro ao processar evento SQS. Mensagem: {}", mensagemJson, e);
            throw new RuntimeException("Erro ao processar mensagem SQS", e);
        }
    }

    /**
     * Extrai o evento da mensagem SQS.
     * Trata os diferentes formatos possíveis:
     * 1. JSON direto do PedidoStatusAlteradoEvent
     * 2. Envelope do EventBridge com campo "detail"
     */
    private PedidoStatusAlteradoEvent extrairEvento(String mensagemJson) throws Exception {
        JsonNode rootNode = objectMapper.readTree(mensagemJson);

        // Se tem o campo "detail", é um envelope do EventBridge
        if (rootNode.has("detail")) {
            log.info("Detectado envelope do EventBridge, extraindo campo 'detail'");
            JsonNode detailNode = rootNode.get("detail");

            if (detailNode.isTextual()) {
                // detail é uma string JSON
                return objectMapper.readValue(detailNode.asText(), PedidoStatusAlteradoEvent.class);
            } else {
                // detail é um objeto JSON
                return objectMapper.treeToValue(detailNode, PedidoStatusAlteradoEvent.class);
            }
        }

        // Se tem codigoPedido, é o evento direto
        if (rootNode.has("codigoPedido")) {
            log.info("Detectado evento direto (sem envelope)");
            return objectMapper.treeToValue(rootNode, PedidoStatusAlteradoEvent.class);
        }

        // Tenta deserializar diretamente
        log.info("Tentando deserializar mensagem diretamente");
        return objectMapper.readValue(mensagemJson, PedidoStatusAlteradoEvent.class);
    }

    private void enviarEmailParaStatus(Pedido pedido, StatusPedido novoStatus) {
        String templateEmail;
        String assunto;

        switch (novoStatus) {
            case CONFIRMADO:
                templateEmail = "emails/pedido-confirmado.html";
                assunto = pedido.getRestaurante().getNome() + " - Pedido confirmado";
                break;
            case ENTREGUE:
                templateEmail = "emails/pedido-entregue.html";
                assunto = pedido.getRestaurante().getNome() + " - Pedido entregue";
                break;
            case CANCELADO:
                templateEmail = "emails/pedido-cancelado.html";
                assunto = pedido.getRestaurante().getNome() + " - Pedido cancelado";
                break;
            default:
                log.info("Status {} não requer envio de email", novoStatus);
                return;
        }

        var mensagemEmail = Mensagem.builder()
                .assunto(assunto)
                .corpo(templateEmail)
                .variavel("pedido", pedido)
                .destinatario(pedido.getCliente().getEmail())
                .build();

        envioEmailService.enviar(mensagemEmail);
        log.info("Email enviado para {} sobre mudança de status do pedido {}",
                pedido.getCliente().getEmail(), pedido.getCodigo());
    }
}
