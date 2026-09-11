package it.uniroma3.siw.memo.configuration;

import it.uniroma3.siw.memo.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration
                .getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {


        http
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource
                        )
                )


                .csrf(csrf ->
                        csrf.ignoringRequestMatchers(
                                "/api/**"
                        )
                )



                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(authorize -> authorize


                        /*
                         * Risorse statiche Thymeleaf
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/css/**",
                                "/images/**",
                                "/favicon.ico"
                        )
                        .permitAll()


                        /*
                         * Pagine Thymeleaf pubbliche
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/register",
                                "/catalogo"
                        )
                        .permitAll()


                        /*
                         * Invio form registrazione
                         * Thymeleaf
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/register"
                        )
                        .permitAll()


                        /*
                         * Pagina di errore Spring
                         */
                        .requestMatchers(
                                "/error"
                        )
                        .permitAll()


                        /*
                         * Swagger / OpenAPI
                         */
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        )
                        .permitAll()


                        /*
                         * Preflight CORS
                         */
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()


                        /*
                         * Login REST utilizzato da React.
                         *
                         * La registrazione REST non serve
                         * più perché viene effettuata
                         * tramite Thymeleaf.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login"
                        )
                        .permitAll()


                        /*
                         * Area personale.
                         *
                         * Questi endpoint devono essere
                         * controllati PRIMA di
                         * /api/decks/**
                         * perché le regole vengono
                         * valutate in ordine.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/decks/mine",
                                "/api/decks/bookmarks"
                        )
                        .authenticated()


                        /*
                         * Lettura pubblica dei deck:
                         *
                         * - home
                         * - ricerca
                         * - dettaglio
                         * - top
                         * - recent
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/decks/**"
                        )
                        .permitAll()


                        /*
                         * Tutto ciò che non è stato
                         * autorizzato sopra richiede
                         * autenticazione.
                         *
                         * Esempi:
                         *
                         * POST /api/decks
                         * PUT /api/decks/{id}
                         * DELETE /api/decks/{id}
                         *
                         * POST like
                         * POST bookmark
                         *
                         * CRUD flashcard
                         *
                         * DELETE account
                         */
                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
}