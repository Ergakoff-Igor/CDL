package ru.docs.construction.catalogue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityBeans {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/catalogue-api/acts")
                        .hasAuthority("SCOPE_edit_actslog")
                        .requestMatchers(HttpMethod.PATCH, "/catalogue-api/acts/{actId:\\d}")
                        .hasAuthority("SCOPE_edit_actslog")
                        .requestMatchers(HttpMethod.DELETE, "/catalogue-api/acts/{actId:\\d}")
                        .hasAuthority("SCOPE_edit_actslog")
                        .requestMatchers(HttpMethod.PATCH, "/catalogue-api/acts/{actId:\\d+}/{status}")
                        .hasAuthority("SCOPE_edit_actslog")
                        .requestMatchers(HttpMethod.GET)
                        .hasAuthority("SCOPE_view_actslog")
                        .anyRequest().denyAll())
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .jwt(Customizer.withDefaults()))
                .build();
    }
}
