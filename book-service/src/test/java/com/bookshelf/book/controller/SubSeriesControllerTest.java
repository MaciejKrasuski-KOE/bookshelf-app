package com.bookshelf.book.controller;

import com.bookshelf.book.dto.CreateSeriesRequest;
import com.bookshelf.book.dto.CreateSubSeriesRequest;
import com.bookshelf.book.grpc.UserGrpcClient;
import com.bookshelf.book.service.SeriesService;
import com.bookshelf.book.service.SubSeriesService;
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
class SubSeriesControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired SubSeriesService subSeriesService;
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

    // ── GET /api/sub-series ────────────────────────────────────────────────────

    @Test
    void listSubSeries_returns200WithAll() throws Exception {
        var discworld = seriesService.create(new CreateSeriesRequest("Discworld"));
        subSeriesService.create(new CreateSubSeriesRequest("City Watch", discworld.id()));

        mockMvc.perform(get("/api/sub-series").header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("City Watch"))
                .andExpect(jsonPath("$[0].series.name").value("Discworld"));
    }

    @Test
    void listSubSeries_returns200FilteredBySeriesId() throws Exception {
        var discworld = seriesService.create(new CreateSeriesRequest("Discworld"));
        var other = seriesService.create(new CreateSeriesRequest("Other Series"));
        subSeriesService.create(new CreateSubSeriesRequest("City Watch", discworld.id()));
        subSeriesService.create(new CreateSubSeriesRequest("Something", other.id()));

        mockMvc.perform(get("/api/sub-series").param("seriesId", discworld.id())
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("City Watch"));
    }

    @Test
    void listSubSeries_returns401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/sub-series"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/sub-series ───────────────────────────────────────────────────

    @Test
    void createSubSeries_returns201() throws Exception {
        var discworld = seriesService.create(new CreateSeriesRequest("Discworld"));

        mockMvc.perform(post("/api/sub-series")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateSubSeriesRequest("City Watch", discworld.id()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("City Watch"))
                .andExpect(jsonPath("$.series.id").value(discworld.id()))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void createSubSeries_returns400_whenNameIsBlank() throws Exception {
        var discworld = seriesService.create(new CreateSeriesRequest("Discworld"));

        mockMvc.perform(post("/api/sub-series")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateSubSeriesRequest("", discworld.id()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSubSeries_returns404_whenSeriesNotFound() throws Exception {
        mockMvc.perform(post("/api/sub-series")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateSubSeriesRequest("City Watch", "nonexistent"))))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/sub-series/{id} ───────────────────────────────────────────────

    @Test
    void getSubSeries_returns200_whenExists() throws Exception {
        var discworld = seriesService.create(new CreateSeriesRequest("Discworld"));
        var created = subSeriesService.create(new CreateSubSeriesRequest("City Watch", discworld.id()));

        mockMvc.perform(get("/api/sub-series/" + created.id())
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.id()))
                .andExpect(jsonPath("$.name").value("City Watch"))
                .andExpect(jsonPath("$.series.name").value("Discworld"));
    }

    @Test
    void getSubSeries_returns404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/sub-series/nonexistent")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/sub-series/{id} ────────────────────────────────────────────

    @Test
    void deleteSubSeries_returns204() throws Exception {
        var discworld = seriesService.create(new CreateSeriesRequest("Discworld"));
        var created = subSeriesService.create(new CreateSubSeriesRequest("City Watch", discworld.id()));

        mockMvc.perform(delete("/api/sub-series/" + created.id())
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSubSeries_returns404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/api/sub-series/nonexistent")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNotFound());
    }
}
