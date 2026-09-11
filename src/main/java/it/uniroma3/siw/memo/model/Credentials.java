package it.uniroma3.siw.memo.model;

import jakarta.persistence.*;

@Entity
public class Credentials {

    public static final String DEFAULT_ROLE = "USER";  // accesso limitato
    public static final String ADMIN_ROLE = "ADMIN"; // accesso completo

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = false)
    private String password;

    @Column(nullable = false)
    private String role =DEFAULT_ROLE;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "utente_id", nullable = false, unique = true)
    private Utente utente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}