package com.bookshelf.book.controller;

import com.bookshelf.book.dto.AuthorDto;
import com.bookshelf.book.dto.CreateAuthorRequest;
import com.bookshelf.book.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public List<AuthorDto> listAuthors() {
        return authorService.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDto createAuthor(@Valid @RequestBody CreateAuthorRequest request) {
        return authorService.create(request);
    }

    @GetMapping("/{id}")
    public AuthorDto getAuthor(@PathVariable String id) {
        return authorService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable String id) {
        authorService.delete(id);
    }
}
