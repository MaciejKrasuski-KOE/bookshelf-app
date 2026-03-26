package com.bookshelf.book.service;

import com.bookshelf.book.dto.CreateSubSeriesRequest;
import com.bookshelf.book.dto.SubSeriesDto;
import com.bookshelf.book.model.Series;
import com.bookshelf.book.model.SubSeries;
import com.bookshelf.book.repository.SeriesRepository;
import com.bookshelf.book.repository.SubSeriesRepository;
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
class SubSeriesServiceTest {

    @Mock SubSeriesRepository subSeriesRepository;
    @Mock SeriesRepository seriesRepository;
    @InjectMocks SubSeriesService subSeriesService;

    // ── listAll ────────────────────────────────────────────────────────────────

    @Test
    void listAll_returnsAllSubSeries() {
        Series discworld = series("s1", "Discworld");
        when(subSeriesRepository.findAll()).thenReturn(List.of(subSeries("ss1", "City Watch", discworld)));

        List<SubSeriesDto> result = subSeriesService.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("City Watch");
        assertThat(result.get(0).series().name()).isEqualTo("Discworld");
    }

    // ── listBySeriesId ─────────────────────────────────────────────────────────

    @Test
    void listBySeriesId_returnsSubSeriesForSeries() {
        Series discworld = series("s1", "Discworld");
        when(subSeriesRepository.findBySeriesId("s1"))
                .thenReturn(List.of(
                        subSeries("ss1", "City Watch", discworld),
                        subSeries("ss2", "Wizards", discworld)));

        List<SubSeriesDto> result = subSeriesService.listBySeriesId("s1");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(SubSeriesDto::name)
                .containsExactlyInAnyOrder("City Watch", "Wizards");
    }

    // ── getById ────────────────────────────────────────────────────────────────

    @Test
    void getById_returnsSubSeries_whenExists() {
        Series discworld = series("s1", "Discworld");
        when(subSeriesRepository.findById("ss1"))
                .thenReturn(Optional.of(subSeries("ss1", "City Watch", discworld)));

        SubSeriesDto result = subSeriesService.getById("ss1");

        assertThat(result.id()).isEqualTo("ss1");
        assertThat(result.name()).isEqualTo("City Watch");
        assertThat(result.series().id()).isEqualTo("s1");
    }

    @Test
    void getById_throwsNotFound_whenMissing() {
        when(subSeriesRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subSeriesService.getById("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("SubSeries not found");
    }

    // ── create ─────────────────────────────────────────────────────────────────

    @Test
    void create_savesAndReturnsSubSeries() {
        Series discworld = series("s1", "Discworld");
        when(seriesRepository.findById("s1")).thenReturn(Optional.of(discworld));
        when(subSeriesRepository.save(any(SubSeries.class)))
                .thenReturn(subSeries("ss1", "City Watch", discworld));

        SubSeriesDto result = subSeriesService.create(new CreateSubSeriesRequest("City Watch", "s1"));

        assertThat(result.name()).isEqualTo("City Watch");
        assertThat(result.series().name()).isEqualTo("Discworld");
        verify(subSeriesRepository).save(any(SubSeries.class));
    }

    @Test
    void create_throwsNotFound_whenSeriesMissing() {
        when(seriesRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subSeriesService.create(new CreateSubSeriesRequest("City Watch", "missing")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Series not found");
    }

    // ── delete ─────────────────────────────────────────────────────────────────

    @Test
    void delete_deletesSubSeries() {
        Series discworld = series("s1", "Discworld");
        SubSeries existing = subSeries("ss1", "City Watch", discworld);
        when(subSeriesRepository.findById("ss1")).thenReturn(Optional.of(existing));

        subSeriesService.delete("ss1");

        verify(subSeriesRepository).delete(existing);
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(subSeriesRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subSeriesService.delete("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("SubSeries not found");
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private Series series(String id, String name) {
        return Series.builder().id(id).name(name).build();
    }

    private SubSeries subSeries(String id, String name, Series series) {
        return SubSeries.builder().id(id).name(name).series(series).build();
    }
}
