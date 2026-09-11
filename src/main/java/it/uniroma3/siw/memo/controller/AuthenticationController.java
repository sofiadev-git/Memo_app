package it.uniroma3.siw.memo.controller;

import it.uniroma3.siw.memo.dto.AuthRequest;
import it.uniroma3.siw.memo.dto.AuthResponse;
import it.uniroma3.siw.memo.model.Credentials;
import it.uniroma3.siw.memo.security.JwtService;
import it.uniroma3.siw.memo.service.CredentialsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final CredentialsService credentialsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationController(CredentialsService credentialsService,
                                    AuthenticationManager authenticationManager,
                                    JwtService jwtService) {
        this.credentialsService = credentialsService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        String username = CredentialsService.normalize(request.username());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password())
        );

        Credentials credentials = credentialsService.find(username);
        String token = jwtService.generateToken(credentials.getUsername(), credentials.getRole());

        return new AuthResponse(token, credentials.getUsername(), credentials.getRole());
    }
}
