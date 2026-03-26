package com.bookshelf.book.service;

import com.bookshelf.book.dto.AuthorDto;
import com.bookshelf.book.dto.BookDto;
import com.bookshelf.book.dto.CreateBookRequest;
import com.bookshelf.book.dto.UpdateBookRequest;
import com.bookshelf.book.model.Author;
import com.bookshelf.book.model.Book;
import com.bookshelf.book.model.BookType;
import com.bookshelf.book.model.Series;
import com.bookshelf.book.model.SubSeries;
import com.bookshelf.book.repository.AuthorRepository;
import com.bookshelf.book.repository.BookRepository;
import com.bookshelf.book.repository.SeriesRepository;
import com.bookshelf.book.repository.SubSeriesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock BookRepository bookRepository;
    @Mock AuthorRepository authorRepository;
    @Mock SeriesRepository seriesRepository;
    @Mock SubSeriesRepository subSeriesRepository;
    @InjectMocks BookService bookService;

    // ── listAll ────────────────────────────────────────────────────────────────

    @Test
    void listAll_returnsAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(
                book("b1", "owner-1", "Clean Code", null, List.of(author("a1", "Robert Martin")), BookType.PAPER, null)));

        List<BookDto> result = bookService.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Clean Code");
        assertThat(result.get(0).getAuthors()).extracting(AuthorDto::name).containsExactly("Robert Martin");
    }

    // ── getById ────────────────────────────────────────────────────────────────

    @Test
    void getById_returnsBook_whenExists() {
        when(bookRepository.findById("b1")).thenReturn(Optional.of(
                book("b1", "owner-1", "Clean Code", null, List.of(author("a1", "Robert Martin")), BookType.PAPER, null)));

        BookDto result = bookService.getById("b1");

        assertThat(result.getId()).isEqualTo("b1");
        assertThat(result.getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void getById_throwsNotFound_whenBookMissing() {
        when(bookRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getById("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Book not found");
    }

    // ── create ─────────────────────────────────────────────────────────────────

    @Test
    void create_savesAndReturnsBook_forPaperBook() {
        Author author = author("a1", "Robert Martin");
        when(authorRepository.findById("a1")).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(
                book("b1", "owner-1", "Clean Code", null, List.of(author), BookType.PAPER, null));

        BookDto result = bookService.create("owner-1", CreateBookRequest.builder()
                .title("Clean Code").authorIds(List.of("a1")).bookType(BookType.PAPER).build());

        assertThat(result.getTitle()).isEqualTo("Clean Code");
        assertThat(result.getBookType()).isEqualTo(BookType.PAPER);
        assertThat(result.getAuthors()).extracting(AuthorDto::name).containsExactly("Robert Martin");
    }

    @Test
    void create_savesAndReturnsBook_withOriginalTitle() {
        Author author = author("a1", "Terry Pratchett");
        when(authorRepository.findById("a1")).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(
                book("b1", "owner-1", "Straż! Straż!", "Guards! Guards!", List.of(author), BookType.PAPER, null));

        BookDto result = bookService.create("owner-1", CreateBookRequest.builder()
                .title("Straż! Straż!").originalTitle("Guards! Guards!")
                .authorIds(List.of("a1")).bookType(BookType.PAPER).build());

        assertThat(result.getTitle()).isEqualTo("Straż! Straż!");
        assertThat(result.getOriginalTitle()).isEqualTo("Guards! Guards!");
    }

    @Test
    void create_savesAndReturnsBook_forEbookWithEshopUrl() {
        Author author = author("a1", "Frank Herbert");
        when(authorRepository.findById("a1")).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(
                book("b2", "owner-1", "Diuna", "Dune", List.of(author), BookType.EBOOK, "https://amazon.com/dune"));

        BookDto result = bookService.create("owner-1", CreateBookRequest.builder()
                .title("Diuna").originalTitle("Dune").authorIds(List.of("a1"))
                .bookType(BookType.EBOOK).eshopUrl("https://amazon.com/dune").build());

        assertThat(result.getEshopUrl()).isEqualTo("https://amazon.com/dune");
    }

    @Test
    void create_throwsBadRequest_whenEshopUrlSetOnPaperBook() {
        assertThatThrownBy(() -> bookService.create("owner-1", CreateBookRequest.builder()
                .title("Clean Code").authorIds(List.of("a1"))
                .bookType(BookType.PAPER).eshopUrl("https://amazon.com").build()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("eshopUrl");
    }

    @Test
    void create_throwsNotFound_whenAuthorMissing() {
        when(authorRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.create("owner-1", CreateBookRequest.builder()
                .title("Clean Code").authorIds(List.of("missing")).bookType(BookType.PAPER).build()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Author not found");
    }

    @Test
    void create_savesBook_withSeriesAndSubSeries() {
        Author author = author("a1", "Terry Pratchett");
        Series discworld = series("s1", "Discworld");
        SubSeries cityWatch = subSeries("ss1", "City Watch", discworld);
        when(authorRepository.findById("a1")).thenReturn(Optional.of(author));
        when(seriesRepository.findById("s1")).thenReturn(Optional.of(discworld));
        when(subSeriesRepository.findById("ss1")).thenReturn(Optional.of(cityWatch));
        Book saved = book("b1", "owner-1", "Straż! Straż!", "Guards! Guards!", List.of(author), BookType.PAPER, null);
        saved.setSeries(discworld);
        saved.setSubSeries(cityWatch);
        saved.setSeriesOrder(8);
        saved.setSubSeriesOrder(1);
        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        BookDto result = bookService.create("owner-1", CreateBookRequest.builder()
                .title("Straż! Straż!").originalTitle("Guards! Guards!")
                .authorIds(List.of("a1")).bookType(BookType.PAPER)
                .seriesId("s1").subSeriesId("ss1").seriesOrder(8).subSeriesOrder(1).build());

        assertThat(result.getSeries().name()).isEqualTo("Discworld");
        assertThat(result.getSubSeries().name()).isEqualTo("City Watch");
        assertThat(result.getSeriesOrder()).isEqualTo(8);
        assertThat(result.getSubSeriesOrder()).isEqualTo(1);
    }

    @Test
    void create_throwsNotFound_whenSeriesMissing() {
        Author author = author("a1", "Terry Pratchett");
        when(authorRepository.findById("a1")).thenReturn(Optional.of(author));
        when(seriesRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.create("owner-1", CreateBookRequest.builder()
                .title("Title").authorIds(List.of("a1")).bookType(BookType.PAPER).seriesId("missing").build()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Series not found");
    }

    // ── update ─────────────────────────────────────────────────────────────────

    @Test
    void update_updatesAndReturnsBook_whenCallerIsOwner() {
        Author author = author("a1", "Robert Martin");
        Book existing = book("b1", "owner-1", "Old Title", null, List.of(author), BookType.PAPER, null);
        when(bookRepository.findById("b1")).thenReturn(Optional.of(existing));
        when(authorRepository.findById("a1")).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        BookDto result = bookService.update("b1", "owner-1", UpdateBookRequest.builder()
                .title("New Title").authorIds(List.of("a1")).bookType(BookType.PAPER).build());

        assertThat(result.getTitle()).isEqualTo("New Title");
    }

    @Test
    void update_throwsForbidden_whenCallerIsNotOwner() {
        Book existing = book("b1", "owner-1", "Clean Code", null, List.of(author("a1", "Robert Martin")), BookType.PAPER, null);
        when(bookRepository.findById("b1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> bookService.update("b1", "other-user", UpdateBookRequest.builder()
                .title("New Title").authorIds(List.of("a1")).bookType(BookType.PAPER).build()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void update_throwsNotFound_whenBookMissing() {
        when(bookRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update("missing", "owner-1", UpdateBookRequest.builder()
                .title("Title").authorIds(List.of("a1")).bookType(BookType.PAPER).build()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Book not found");
    }

    @Test
    void update_throwsBadRequest_whenEshopUrlSetOnPaperBook() {
        Book existing = book("b1", "owner-1", "Clean Code", null, List.of(author("a1", "Robert Martin")), BookType.PAPER, null);
        when(bookRepository.findById("b1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> bookService.update("b1", "owner-1", UpdateBookRequest.builder()
                .title("Clean Code").authorIds(List.of("a1"))
                .bookType(BookType.PAPER).eshopUrl("https://amazon.com").build()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("eshopUrl");
    }

    // ── delete ─────────────────────────────────────────────────────────────────

    @Test
    void delete_deletesBook_whenCallerIsOwner() {
        Book existing = book("b1", "owner-1", "Clean Code", null, List.of(author("a1", "Robert Martin")), BookType.PAPER, null);
        when(bookRepository.findById("b1")).thenReturn(Optional.of(existing));

        bookService.delete("b1", "owner-1");

        verify(bookRepository).delete(existing);
    }

    @Test
    void delete_throwsForbidden_whenCallerIsNotOwner() {
        Book existing = book("b1", "owner-1", "Clean Code", null, List.of(author("a1", "Robert Martin")), BookType.PAPER, null);
        when(bookRepository.findById("b1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> bookService.delete("b1", "other-user"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void delete_throwsNotFound_whenBookMissing() {
        when(bookRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.delete("missing", "owner-1"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Book not found");
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private Author author(String id, String name) {
        return Author.builder().id(id).name(name).build();
    }

    private Series series(String id, String name) {
        return Series.builder().id(id).name(name).build();
    }

    private SubSeries subSeries(String id, String name, Series series) {
        return SubSeries.builder().id(id).name(name).series(series).build();
    }

    private Book book(String id, String ownerId, String title, String originalTitle,
                      List<Author> authors, BookType type, String eshopUrl) {
        return Book.builder()
                .id(id).ownerId(ownerId).title(title).originalTitle(originalTitle)
                .authors(new ArrayList<>(authors)).bookType(type).eshopUrl(eshopUrl)
                .build();
    }
}
