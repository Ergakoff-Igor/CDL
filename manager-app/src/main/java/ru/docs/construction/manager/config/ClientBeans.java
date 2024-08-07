package ru.docs.construction.manager.config;

import ru.docs.construction.manager.client.RestClientActsRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    @Bean
    public RestClientActsRestClient actsRestClient(
            @Value("${actslog.service.catalogue.uri:http://localhost:8081}") String catalogueBaseUri) {
        return new RestClientActsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .build());
    }
}
