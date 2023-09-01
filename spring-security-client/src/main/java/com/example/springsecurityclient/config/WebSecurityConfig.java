package com.example.springsecurityclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] WHITE_LIST_URLS = {
            "/hello",
            "/register"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
               .authorizeHttpRequests(r -> {
                   r.requestMatchers("/hello", "/register").permitAll();
                  // r.requestMatchers("/api/**").authenticated();
               })
               .cors(c -> {
                   var corsConfig = new CorsConfiguration();
                   corsConfig.addAllowedHeader(CorsConfiguration.ALL);
                   corsConfig.addAllowedOrigin(CorsConfiguration.ALL);
                   corsConfig.addAllowedMethod(CorsConfiguration.ALL);
               });
               //.oauth2Login(oauth2login -> oauth2login.loginPage("/oauth2/authorization/api-client-oidc"))
              // .oauth2Client(Customizer.withDefaults());
       return http.build();
    }
}
