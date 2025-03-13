package com.example.reactive.dto.login;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefreshToken(long id, String token, LocalDateTime expiryDate, String username) {
}
