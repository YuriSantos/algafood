package com.algaworks.algafood.infrastructure.service.email;

import com.algaworks.algafood.core.email.EmailProperties;
import com.algaworks.algafood.domain.service.EnvioEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service; // Removida a anotação @Service
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Slf4j
// @Service // Removida a anotação @Service
public class SesEnvioEmailService implements EnvioEmailService {

    @Autowired
    private SesClient sesClient;

    @Autowired
    private EmailProperties emailProperties;

    @Override
    public void enviar(Mensagem mensagem) {
        try {
            String corpoHtml = processarTemplate(mensagem);
            
            SendEmailRequest request = SendEmailRequest.builder()
                .source(emailProperties.getRemetente())
                .destination(Destination.builder()
                    .toAddresses(mensagem.getDestinatarios())
                    .build())
                .message(Message.builder()
                    .subject(Content.builder().data(mensagem.getAssunto()).build())
                    .body(Body.builder().html(Content.builder().data(corpoHtml).build()).build())
                    .build())
                .build();

            sesClient.sendEmail(request);
            
            log.info("E-mail enviado com sucesso para: {}", mensagem.getDestinatarios());
        } catch (Exception e) {
            throw new EmailException("Não foi possível enviar e-mail pelo Amazon SES.", e);
        }
    }

    // O método de processar o template com FreeMarker já deve existir em outra implementação.
    // Por simplicidade, estou apenas retornando o corpo da mensagem.
    // Se você tiver uma classe de template, podemos injetá-la aqui.
    protected String processarTemplate(Mensagem mensagem) {
        // Implemente a lógica de processamento do template aqui, se necessário.
        // Por enquanto, apenas retornamos o corpo.
        return mensagem.getCorpo();
    }
}
