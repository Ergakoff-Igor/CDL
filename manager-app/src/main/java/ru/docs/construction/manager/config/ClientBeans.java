package ru.docs.construction.manager.config;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import ru.docs.construction.manager.security.OAuthClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import ru.docs.construction.manager.client.RestClientActsRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    @Bean
    public RestClientActsRestClient actsRestClient(
            @Value("${acts-log.service.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${acts-log.services.catalogue.registration-id:keycloak}") String registrationId) {
        return new RestClientActsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestInterceptor(
                        new OAuthClientHttpRequestInterceptor(
                                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
                                        authorizedClientRepository), registrationId))
                .build());
    }
}
