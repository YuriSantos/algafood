package com.algaworks.algafood.core.security.authorizationserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/logout")
public class RevocationController {

    @Autowired
    private OAuth2AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<Void> revoke(@RequestParam("token") String token,
                                       @RequestParam(name = "token_type_hint", required = false) String tokenTypeHint) {
        
        log.info("Recebida requisição de logout/revogação para o token: {}", token);
        
        OAuth2Authorization authorization = authorizationService.findByToken(token, null);

        if (authorization != null) {
            log.info("Autorização encontrada. Removendo...");
            authorizationService.remove(authorization);
            log.info("Autorização removida com sucesso.");
        } else {
            log.warn("Nenhuma autorização encontrada para o token informado.");
            // Tenta procurar especificamente como access token se a busca genérica falhar
            authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
            if (authorization != null) {
                log.info("Autorização encontrada como ACCESS_TOKEN. Removendo...");
                authorizationService.remove(authorization);
            }
        }

        return ResponseEntity.ok().build();
    }
}
