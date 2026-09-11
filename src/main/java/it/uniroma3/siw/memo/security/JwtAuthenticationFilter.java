package it.uniroma3.siw.memo.security;

import it.uniroma3.siw.memo.service.CredentialsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CredentialsService credentialsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CredentialsService credentialsService) {

        this.jwtService = jwtService;
        this.credentialsService = credentialsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header =
                request.getHeader("Authorization");

        if (header != null
                && header.startsWith("Bearer ")
                && SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            String token = header.substring(7);

            try {

                String username =
                        jwtService.extractUsername(token);

                UserDetails userDetails =
                        credentialsService
                                .loadUserByUsername(username);

                if (jwtService.isValid(
                        token,
                        userDetails.getUsername())) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }

            } catch (Exception ignored) {
                // Token assente, scaduto o non valido:
                // la richiesta continua come anonima.
            }
        }

        filterChain.doFilter(request, response);
    }
}