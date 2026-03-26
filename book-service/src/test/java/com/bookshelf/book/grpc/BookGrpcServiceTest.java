package com.bookshelf.book.grpc;

import com.bookshelf.book.dto.AuthorDto;
import com.bookshelf.book.dto.BookDto;
import com.bookshelf.book.model.BookType;
import com.bookshelf.book.service.BookService;
import com.bookshelf.grpc.book.*;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookGrpcServiceTest {

    @Mock BookService bookService;
    @InjectMocks BookGrpcService bookGrpcService;

    private static final LocalDateTime NOW = LocalDateTime.of(2025, 1, 1, 12, 0);

    // ── getBook ────────────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void getBook_sendsBookResponseAndCompletes_whenExists() {
        BookDto dto = BookDto.builder()
                .id("b1").ownerId("owner-1").title("Clean Code")
                .authors(List.of(new AuthorDto("a1", "Robert Martin")))
                .bookType(BookType.PAPER).createdAt(NOW).build();
        when(bookService.getById("b1")).thenReturn(dto);
        StreamObserver<BookResponse> responseObserver = mock(StreamObserver.class);

        bookGrpcService.getBook(GetBookRequest.newBuilder().setBookId("b1").build(), responseObserver);

        ArgumentCaptor<BookResponse> captor = ArgumentCaptor.forClass(BookResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());

        assertThat(captor.getValue().getBookId()).isEqualTo("b1");
        assertThat(captor.getValue().getTitle()).isEqualTo("Clean Code");
        assertThat(captor.getValue().getBookType()).isEqualTo("PAPER");
        assertThat(captor.getValue().getAuthorsList()).containsExactly("Robert Martin");
    }

    @Test
    @SuppressWarnings("unchecked")
    void getBook_sendsError_whenNotFound() {
        when(bookService.getById("missing")).thenThrow(mock(ResponseStatusException.class));
        StreamObserver<BookResponse> responseObserver = mock(StreamObserver.class);

        bookGrpcService.getBook(GetBookRequest.newBuilder().setBookId("missing").build(), responseObserver);

        verify(responseObserver).onError(any());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();
    }

    // ── listBooks ──────────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void listBooks_sendsAllBooksAndCompletes() {
        BookDto dto = BookDto.builder()
                .id("b1").ownerId("owner-1").title("Diuna").originalTitle("Dune")
                .authors(List.of(new AuthorDto("a1", "Frank Herbert")))
                .bookType(BookType.EBOOK).eshopUrl("https://amazon.com").createdAt(NOW).build();
        when(bookService.listAll()).thenReturn(List.of(dto));
        StreamObserver<BooksResponse> responseObserver = mock(StreamObserver.class);

        bookGrpcService.listBooks(ListBooksRequest.newBuilder().build(), responseObserver);

        ArgumentCaptor<BooksResponse> captor = ArgumentCaptor.forClass(BooksResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());

        assertThat(captor.getValue().getBooksList()).hasSize(1);
        assertThat(captor.getValue().getBooks(0).getTitle()).isEqualTo("Diuna");
        assertThat(captor.getValue().getBooks(0).getEshopUrl()).isEqualTo("https://amazon.com");
        assertThat(captor.getValue().getBooks(0).getAuthorsList()).containsExactly("Frank Herbert");
    }
}
