package com.arikok.rangereverseproxy.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("keycloak")
@Configuration
@Getter @Setter
public class KeycloakProperties {

    private String realm;
    private String clientId;
    private String clientSecret;
    private String authServerUrl;
    private int reissueTokenMargin;

}
