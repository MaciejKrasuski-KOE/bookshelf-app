package com.bookshelf.book.service;

import com.bookshelf.book.dto.AuthorDto;
import com.bookshelf.book.dto.CreateAuthorRequest;
import com.bookshelf.book.model.Author;
import com.bookshelf.book.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<AuthorDto> listAll() {
        return authorRepository.findAll().stream().map(this::toDto).toList();
    }

    public AuthorDto getById(String id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public AuthorDto create(CreateAuthorRequest request) {
        Author author = Author.builder().name(request.name()).build();
        return toDto(authorRepository.save(author));
    }

    @Transactional
    public void delete(String id) {
        authorRepository.delete(findOrThrow(id));
    }

    public AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getName());
    }

    private Author findOrThrow(String id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
    }
}
