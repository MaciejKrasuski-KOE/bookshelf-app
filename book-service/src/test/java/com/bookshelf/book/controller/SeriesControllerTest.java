package com.bookshelf.book.controller;

import com.bookshelf.book.dto.CreateSeriesRequest;
import com.bookshelf.book.grpc.UserGrpcClient;
import com.bookshelf.book.service.SeriesService;
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
class SeriesControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired SeriesService seriesService;

    @MockBean UserGrpcClient userGrpcClient;

    private static final String TOKEN = "test-token";
    private static final String USER_ID = "user-123";

    @BeforeEach
    void stubTokenValidation() {
        ValidateTokenResponse validResponse = ValidateTokenResponse.newBuilder()
                .setValid(true).setUserId(USER_ID).setUsername("alice").build();
        when(userGrpcClient.validateToken(TOKEN)).thenReturn(validResponse);
    }

    // ── GET /api/series ────────────────────────────────────────────────────────

    @Test
    void listSeries_returns200WithSeries() throws Exception {
        seriesService.create(new CreateSeriesRequest("Discworld"));

        mockMvc.perform(get("/api/series").header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Discworld"));
    }

    @Test
    void listSeries_returns401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/series"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/series ───────────────────────────────────────────────────────

    @Test
    void createSeries_returns201() throws Exception {
        mockMvc.perform(post("/api/series")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateSeriesRequest("Discworld"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Discworld"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void createSeries_returns400_whenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/series")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/series/{id} ───────────────────────────────────────────────────

    @Test
    void getSeries_returns200_whenExists() throws Exception {
        var created = seriesService.create(new CreateSeriesRequest("Discworld"));

        mockMvc.perform(get("/api/series/" + created.id())
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.id()))
                .andExpect(jsonPath("$.name").value("Discworld"));
    }

    @Test
    void getSeries_returns404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/series/nonexistent")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/series/{id} ────────────────────────────────────────────────

    @Test
    void deleteSeries_returns204() throws Exception {
        var created = seriesService.create(new CreateSeriesRequest("Discworld"));

        mockMvc.perform(delete("/api/series/" + created.id())
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSeries_returns404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/api/series/nonexistent")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNotFound());
    }
}
