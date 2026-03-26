package com.bookshelf.book.controller;

import com.bookshelf.book.dto.CreateAuthorRequest;
import com.bookshelf.book.grpc.UserGrpcClient;
import com.bookshelf.book.service.AuthorService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthorControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AuthorService authorService;

    @MockBean UserGrpcClient userGrpcClient;

    private static final String TOKEN = "test-token";
    private static final String USER_ID = "user-123";

    @BeforeEach
    void stubTokenValidation() {
        ValidateTokenResponse validResponse = ValidateTokenResponse.newBuilder()
                .setValid(true).setUserId(USER_ID).setUsername("alice").build();
        when(userGrpcClient.validateToken(TOKEN)).thenReturn(validResponse);
    }

    // ── GET /api/authors ───────────────────────────────────────────────────────

    @Test
    void listAuthors_returns200WithAuthors() throws Exception {
        authorService.create(new CreateAuthorRequest("Terry Pratchett"));

        mockMvc.perform(get("/api/authors").header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Terry Pratchett"));
    }

    @Test
    void listAuthors_returns401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/authors ──────────────────────────────────────────────────────

    @Test
    void createAuthor_returns201() throws Exception {
        mockMvc.perform(post("/api/authors")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateAuthorRequest("Terry Pratchett"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Terry Pratchett"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void createAuthor_returns400_whenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/authors")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/authors/{id} ──────────────────────────────────────────────────

    @Test
    void getAuthor_returns200_whenExists() throws Exception {
        var created = authorService.create(new CreateAuthorRequest("Terry Pratchett"));

        mockMvc.perform(get("/api/authors/" + created.id())
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.id()))
                .andExpect(jsonPath("$.name").value("Terry Pratchett"));
    }

    @Test
    void getAuthor_returns404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/authors/nonexistent")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/authors/{id} ───────────────────────────────────────────────

    @Test
    void deleteAuthor_returns204() throws Exception {
        var created = authorService.create(new CreateAuthorRequest("Terry Pratchett"));

        mockMvc.perform(delete("/api/authors/" + created.id())
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAuthor_returns404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/api/authors/nonexistent")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNotFound());
    }
}
