package jobtrackerapp.servicenotifications.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,"/notification/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/add-candidatures").permitAll()
                        // 2. Toute autre requête nécessite une authentification
                        .anyRequest().authenticated()
                )
                // Désactive CSRF pour les API REST sans état (si vous utilisez JWT ou Basic Auth)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
