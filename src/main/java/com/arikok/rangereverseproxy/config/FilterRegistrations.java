package com.arikok.rangereverseproxy.config;

import com.arikok.rangereverseproxy.filter.KeycloakAuthenticationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

public class FilterRegistrations {

    @Bean
    @ConditionalOnProperty(value = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
    public KeycloakAuthenticationFilter keycloakAuthenticationFilter() {
        return new KeycloakAuthenticationFilter();
    }
}
