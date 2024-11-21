package ru.docs.construction.catalogue.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("swagger")
public class SpringDocBeans {

    @Configuration
    @SecurityScheme(
            name = "keycloak",
            type = SecuritySchemeType.OAUTH2,
            flows = @OAuthFlows(
                    authorizationCode = @OAuthFlow(
                            authorizationUrl = "${keycloak.uri}/realms/actslog/protocol/openid-connect/auth",
                            tokenUrl = "${keycloak.uri}/realms/actslog/protocol/openid-connect/token",
                            scopes = {
                                    @OAuthScope(name = "openid"),
                                    @OAuthScope(name = "microprofile-jwt"),
                                    @OAuthScope(name = "edit_actslog"),
                                    @OAuthScope(name = "view_actslog")
                            }
                    ))
    )
    @OpenAPIDefinition(
            info = @Info(
                    title = "Construction Docks Log",
                    description = "Журнал учета строительной документации",
                    version = "1.0.0",
                    contact = @Contact(
                            name = "Ergakov Igor",
                            email = "i.ergakoff@mail.ru",
                            url = "https://github.com/Ergakoff-Igor"
                    )
            )
    )
    public static class SpringDocSecurity {
    }
}
