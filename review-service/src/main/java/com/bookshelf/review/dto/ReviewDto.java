package com.bookshelf.review.dto;

import java.time.LocalDateTime;

public record ReviewDto(
        String id,
        String userId,
        String bookId,
        int rating,
        String content,
        boolean verifiedReader,
        LocalDateTime createdAt
) {}
