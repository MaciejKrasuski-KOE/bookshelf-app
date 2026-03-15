package com.bookshelf.user.dto;

public record AuthResponse(
        String token,
        String username,
        String userId
) {}
