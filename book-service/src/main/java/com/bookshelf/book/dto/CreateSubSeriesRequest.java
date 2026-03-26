package com.bookshelf.book.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSubSeriesRequest(@NotBlank String name, @NotBlank String seriesId) {}
