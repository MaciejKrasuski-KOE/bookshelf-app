package com.bookshelf.book.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSeriesRequest(@NotBlank String name) {}
