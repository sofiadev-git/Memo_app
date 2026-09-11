package it.uniroma3.siw.memo.dto;

public record AuthResponse(
        String token,
        String username,
        String role
) {
}
