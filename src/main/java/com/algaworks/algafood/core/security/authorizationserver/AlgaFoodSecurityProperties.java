package com.algaworks.algafood.core.security.authorizationserver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Component
@Validated
@ConfigurationProperties("algafood.auth")
public class AlgaFoodSecurityProperties {

    @NotBlank
    private String providerUrl;

    private OpaqueToken opaqueToken = new OpaqueToken();

    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public OpaqueToken getOpaqueToken() {
        return opaqueToken;
    }

    public void setOpaqueToken(OpaqueToken opaqueToken) {
        this.opaqueToken = opaqueToken;
    }

    public static class OpaqueToken {
        @NotBlank
        private String introspectionUri;
        @NotBlank
        private String clientId;
        @NotBlank
        private String clientSecret;

        public String getIntrospectionUri() {
            return introspectionUri;
        }

        public void setIntrospectionUri(String introspectionUri) {
            this.introspectionUri = introspectionUri;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }

}
