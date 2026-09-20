package com.gridpulse.authservice.auth;

public record LoginResponse(String token, String tokenType, String username, String role) {
}
