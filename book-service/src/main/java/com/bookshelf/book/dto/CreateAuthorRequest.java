package com.bookshelf.book.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAuthorRequest(@NotBlank String name) {}
