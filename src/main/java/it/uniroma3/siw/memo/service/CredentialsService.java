package it.uniroma3.siw.memo.service;

import it.uniroma3.siw.memo.model.Credentials;
import it.uniroma3.siw.memo.model.Utente;
import it.uniroma3.siw.memo.repository.CredentialsRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class CredentialsService implements UserDetailsService {

    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;

    public CredentialsService(CredentialsRepository credentialsRepository,
                              PasswordEncoder passwordEncoder) {
        this.credentialsRepository = credentialsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static String normalize(String username) {
        return username == null
                ? ""
                : username.trim().toLowerCase(Locale.ROOT);
    }

    @Transactional
    public Credentials register(String username, String password) {

        String normalized = normalize(username);

        if (normalized.isBlank()
                || password == null
                || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Username e password sono obbligatori");
        }

        if (credentialsRepository.existsByUsername(normalized)) {
            throw new IllegalArgumentException(
                    "Username già utilizzato");
        }

        Utente user = new Utente();

        Credentials credentials = new Credentials();
        credentials.setUsername(normalized);
        credentials.setPassword(
                passwordEncoder.encode(password));
        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setUtente(user);

        user.setCredentials(credentials);

        return credentialsRepository.save(credentials);
    }

    @Transactional(readOnly = true)
    public Credentials find(String username) {

        return credentialsRepository
                .findByUsername(normalize(username))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Utente non trovato"));
    }

    @Transactional(readOnly = true)
    public boolean exists(String username) {

        return credentialsRepository
                .existsByUsername(normalize(username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Credentials credentials = credentialsRepository
                .findByUsername(normalize(username))
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Utente non trovato"));

        return User
                .withUsername(credentials.getUsername())
                .password(credentials.getPassword())
                .authorities(credentials.getRole())
                .build();
    }
}