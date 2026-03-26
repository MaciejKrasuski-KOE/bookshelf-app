package com.bookshelf.book.controller;

import com.bookshelf.book.dto.CreateAuthorRequest;
import com.bookshelf.book.dto.CreateBookRequest;
import com.bookshelf.book.dto.UpdateBookRequest;
import com.bookshelf.book.grpc.UserGrpcClient;
import com.bookshelf.book.model.BookType;
import com.bookshelf.book.service.AuthorService;
import com.bookshelf.book.service.BookService;
import com.bookshelf.grpc.user.ValidateTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired BookService bookService;
    @Autowired AuthorService authorService;

    @MockBean UserGrpcClient userGrpcClient;

    private static final String TOKEN = "test-token";
    private static final String USER_ID = "user-123";
    private static final String OTHER_USER_ID = "user-999";

    @BeforeEach
    void stubTokenValidation() {
        ValidateTokenResponse validResponse = ValidateTokenResponse.newBuilder()
                .setValid(true)
                .setUserId(USER_ID)
                .setUsername("alice")
                .build();
        when(userGrpcClient.validateToken(TOKEN)).thenReturn(validResponse);
    }

    // ── GET /api/books ─────────────────────────────────────────────────────────

    @Test
    void listBooks_returns200WithBooks() throws Exception {
        var author = authorService.create(new CreateAuthorRequest("Robert Martin"));
        bookService.create(USER_ID, CreateBookRequest.builder()
                .title("Clean Code").authorIds(List.of(author.id())).bookType(BookType.PAPER).build());

        mockMvc.perform(get("/api/books").header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[0].authors[0].name").value("Robert Martin"));
    }

    @Test
    void listBooks_returns401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/books ────────────────────────────────────────────────────────

    @Test
    void createBook_returns201_forPaperBook() throws Exception {
        var author = authorService.create(new CreateAuthorRequest("Robert Martin"));
        var request = CreateBookRequest.builder()
                .title("Clean Code").authorIds(List.of(author.id())).bookType(BookType.PAPER).build();

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.bookType").value("PAPER"))
                .andExpect(jsonPath("$.ownerId").value(USER_ID))
                .andExpect(jsonPath("$.authors[0].name").value("Robert Martin"));
    }

    @Test
    void createBook_returns201_withOriginalTitle() throws Exception {
        var author = authorService.create(new CreateAuthorRequest("Terry Pratchett"));
        var request = CreateBookRequest.builder()
                .title("Straż! Straż!").originalTitle("Guards! Guards!")
                .authorIds(List.of(author.id())).bookType(BookType.PAPER).build();

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Straż! Straż!"))
                .andExpect(jsonPath("$.originalTitle").value("Guards! Guards!"));
    }

    @Test
    void createBook_returns201_forEbookWithEshopUrl() throws Exception {
        var author = authorService.create(new CreateAuthorRequest("Frank Herbert"));
        var request = CreateBookRequest.builder()
                .title("Diuna").originalTitle("Dune").authorIds(List.of(author.id()))
                .bookType(BookType.EBOOK).eshopUrl("https://amazon.com/dune").build();

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookType").value("EBOOK"))
                .andExpect(jsonPath("$.eshopUrl").value("https://amazon.com/dune"));
    }

    @Test
    void createBook_returns400_whenEshopUrlSetOnPaperBook() throws Exception {
        var author = authorService.create(new CreateAuthorRequest("Robert Martin"));
        var request = CreateBookRequest.builder()
                .title("Clean Code").authorIds(List.of(author.id()))
                .bookType(BookType.PAPER).eshopUrl("https://amazon.com").build();

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBook_returns400_whenTitleIsBlank() throws Exception {
        var author = authorService.create(new CreateAuthorRequest("Robert Martin"));

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"authorIds\":[\"" + author.id() + "\"],\"bookType\":\"PAPER\"}"))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/books/{id} ────────────────────────────────────────────────────

    @Test
    void getBook_returns200_whenBookExists() throws Exception {
        var author = authorService.create(new CreateAuthorRequest("Frank Herbert"));
        var created = bookService.create(USER_ID, CreateBookRequest.builder()
                .title("Diuna").originalTitle("Dune").authorIds(List.of(author.id())).bookType(BookType.PAPER).build());

        mockMvc.perform(get("/api/books/" + created.getId())
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.title").value("Diuna"))
                .andExpect(jsonPath("$.originalTitle").value("Dune"));
    }

    @Test
    void getBook_returns404_whenBookNotFound() throws Exception {
        mockMvc.perform(get("/api/books/nonexistent")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/books/{id} ────────────────────────────────────────────────────

    @Test
    void updateBook_returns200_whenCallerIsOwner() throws Exception {
        var author = authorService.create(new CreateAuthorRequest("Robert Martin"));
        var created = bookService.create(USER_ID, CreateBookRequest.builder()
                .title("Old Title").authorIds(List.of(author.id())).bookType(BookType.PAPER).build());
        var update = UpdateBookRequest.builder()
                .title("New Title").authorIds(List.of(author.id())).bookType(BookType.PAPER).build();

        mockMvc.perform(put("/api/books/" + created.getId())
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    void updateBook_returns403_whenCallerIsNotOwner() throws Exception {
        var author = authorService.create(new CreateAuthorRequest("Robert Martin"));
        var created = bookService.create(OTHER_USER_ID, CreateBookRequest.builder()
                .title("Title").authorIds(List.of(author.id())).bookType(BookType.PAPER).build());
        var update = UpdateBookRequest.builder()
                .title("Hacked Title").authorIds(List.of(author.id())).bookType(BookType.PAPER).build();

        mockMvc.perform(put("/api/books/" + created.getId())
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /api/books/{id} ─────────────────────────────────────────────────

    @Test
    void deleteBook_returns204_whenCallerIsOwner() throws Exception {
        var author = authorService.create(new CreateAuthorRequest("Robert Martin"));
        var created = bookService.create(USER_ID, CreateBookRequest.builder()
                .title("Title").authorIds(List.of(author.id())).bookType(BookType.PAPER).build());

        mockMvc.perform(delete("/api/books/" + created.getId())
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_returns403_whenCallerIsNotOwner() throws Exception {
        var author = authorService.create(new CreateAuthorRequest("Robert Martin"));
        var created = bookService.create(OTHER_USER_ID, CreateBookRequest.builder()
                .title("Title").authorIds(List.of(author.id())).bookType(BookType.PAPER).build());

        mockMvc.perform(delete("/api/books/" + created.getId())
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isForbidden());
    }
}
