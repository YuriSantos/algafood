package com.algaworks.algafood.core.security;

import com.algaworks.algafood.core.security.authorizationserver.AlgaFoodSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(Customizer.withDefaults())
            .csrf().disable()
            .cors().and()
            .oauth2ResourceServer().opaqueToken();

        return http.build();
    }

    @Bean
    public OpaqueTokenIntrospector opaqueTokenIntrospector(AlgaFoodSecurityProperties properties) {
        return new SpringOpaqueTokenIntrospector(
                properties.getOpaqueToken().getIntrospectionUri(),
                properties.getOpaqueToken().getClientId(),
                properties.getOpaqueToken().getClientSecret()) {
            
            @Override
            public org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal introspect(String token) {

                try {
                    org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal principal = super.introspect(token);
                    Collection<GrantedAuthority> authorities = new ArrayList<>(principal.getAuthorities());
                    
                    List<String> customAuthorities = principal.getAttribute("authorities");
                    
                    if (customAuthorities != null) {
                        authorities.addAll(customAuthorities.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()));
                    }
                    
                    return new org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal(
                            principal.getName(), principal.getAttributes(), authorities);
                } catch (Exception e) {
                    log.error("Erro durante a introspecção do token", e);
                    throw e;
                }
            }
        };
    }

}
