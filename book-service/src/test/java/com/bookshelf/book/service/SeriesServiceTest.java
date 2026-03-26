package com.bookshelf.book.service;

import com.bookshelf.book.dto.CreateSeriesRequest;
import com.bookshelf.book.dto.SeriesDto;
import com.bookshelf.book.model.Series;
import com.bookshelf.book.repository.SeriesRepository;
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
class SeriesServiceTest {

    @Mock SeriesRepository seriesRepository;
    @InjectMocks SeriesService seriesService;

    // ── listAll ────────────────────────────────────────────────────────────────

    @Test
    void listAll_returnsAllSeries() {
        when(seriesRepository.findAll()).thenReturn(List.of(series("s1", "Discworld")));

        List<SeriesDto> result = seriesService.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Discworld");
    }

    // ── getById ────────────────────────────────────────────────────────────────

    @Test
    void getById_returnsSeries_whenExists() {
        when(seriesRepository.findById("s1")).thenReturn(Optional.of(series("s1", "Discworld")));

        SeriesDto result = seriesService.getById("s1");

        assertThat(result.id()).isEqualTo("s1");
        assertThat(result.name()).isEqualTo("Discworld");
    }

    @Test
    void getById_throwsNotFound_whenMissing() {
        when(seriesRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seriesService.getById("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Series not found");
    }

    // ── create ─────────────────────────────────────────────────────────────────

    @Test
    void create_savesAndReturnsSeries() {
        when(seriesRepository.save(any(Series.class))).thenReturn(series("s1", "Discworld"));

        SeriesDto result = seriesService.create(new CreateSeriesRequest("Discworld"));

        assertThat(result.name()).isEqualTo("Discworld");
        verify(seriesRepository).save(any(Series.class));
    }

    // ── delete ─────────────────────────────────────────────────────────────────

    @Test
    void delete_deletesSeries() {
        Series existing = series("s1", "Discworld");
        when(seriesRepository.findById("s1")).thenReturn(Optional.of(existing));

        seriesService.delete("s1");

        verify(seriesRepository).delete(existing);
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(seriesRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seriesService.delete("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Series not found");
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private Series series(String id, String name) {
        return Series.builder().id(id).name(name).build();
    }
}
