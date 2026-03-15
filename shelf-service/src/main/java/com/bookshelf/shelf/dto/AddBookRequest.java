package com.bookshelf.shelf.dto;

import jakarta.validation.constraints.NotBlank;

public record AddBookRequest(
        @NotBlank String bookId
) {}
