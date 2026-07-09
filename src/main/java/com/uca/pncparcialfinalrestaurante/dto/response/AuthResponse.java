package com.uca.pncparcialfinalrestaurante.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
    public AuthResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}