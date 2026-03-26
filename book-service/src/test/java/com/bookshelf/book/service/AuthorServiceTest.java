package com.bookshelf.book.service;

import com.bookshelf.book.dto.AuthorDto;
import com.bookshelf.book.dto.CreateAuthorRequest;
import com.bookshelf.book.model.Author;
import com.bookshelf.book.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock AuthorRepository authorRepository;
    @InjectMocks AuthorService authorService;

    // ── listAll ────────────────────────────────────────────────────────────────

    @Test
    void listAll_returnsAllAuthors() {
        when(authorRepository.findAll()).thenReturn(List.of(author("a1", "Terry Pratchett")));

        List<AuthorDto> result = authorService.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Terry Pratchett");
    }

    // ── getById ────────────────────────────────────────────────────────────────

    @Test
    void getById_returnsAuthor_whenExists() {
        when(authorRepository.findById("a1")).thenReturn(Optional.of(author("a1", "Terry Pratchett")));

        AuthorDto result = authorService.getById("a1");

        assertThat(result.id()).isEqualTo("a1");
        assertThat(result.name()).isEqualTo("Terry Pratchett");
    }

    @Test
    void getById_throwsNotFound_whenMissing() {
        when(authorRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.getById("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Author not found");
    }

    // ── create ─────────────────────────────────────────────────────────────────

    @Test
    void create_savesAndReturnsAuthor() {
        when(authorRepository.save(any(Author.class))).thenReturn(author("a1", "Terry Pratchett"));

        AuthorDto result = authorService.create(new CreateAuthorRequest("Terry Pratchett"));

        assertThat(result.name()).isEqualTo("Terry Pratchett");
        verify(authorRepository).save(any(Author.class));
    }

    // ── delete ─────────────────────────────────────────────────────────────────

    @Test
    void delete_deletesAuthor() {
        Author existing = author("a1", "Terry Pratchett");
        when(authorRepository.findById("a1")).thenReturn(Optional.of(existing));

        authorService.delete("a1");

        verify(authorRepository).delete(existing);
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(authorRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.delete("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Author not found");
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private Author author(String id, String name) {
        return Author.builder().id(id).name(name).build();
    }
}
