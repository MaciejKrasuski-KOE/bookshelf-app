package com.bookshelf.shelf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateShelfRequest(
        @NotBlank @Size(min = 1, max = 100) String name
) {}
