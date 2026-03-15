package com.bookshelf.shelf.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ShelfDto(
        String id,
        String userId,
        String name,
        List<String> bookIds,
        LocalDateTime createdAt
) {}
