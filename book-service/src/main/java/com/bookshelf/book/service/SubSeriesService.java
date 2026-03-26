package com.bookshelf.book.service;

import com.bookshelf.book.dto.CreateSubSeriesRequest;
import com.bookshelf.book.dto.SeriesDto;
import com.bookshelf.book.dto.SubSeriesDto;
import com.bookshelf.book.model.Series;
import com.bookshelf.book.model.SubSeries;
import com.bookshelf.book.repository.SeriesRepository;
import com.bookshelf.book.repository.SubSeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubSeriesService {

    private final SubSeriesRepository subSeriesRepository;
    private final SeriesRepository seriesRepository;

    public List<SubSeriesDto> listAll() {
        return subSeriesRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<SubSeriesDto> listBySeriesId(String seriesId) {
        return subSeriesRepository.findBySeriesId(seriesId).stream().map(this::toDto).toList();
    }

    public SubSeriesDto getById(String id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public SubSeriesDto create(CreateSubSeriesRequest request) {
        Series series = seriesRepository.findById(request.seriesId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found"));
        SubSeries subSeries = SubSeries.builder().name(request.name()).series(series).build();
        return toDto(subSeriesRepository.save(subSeries));
    }

    @Transactional
    public void delete(String id) {
        subSeriesRepository.delete(findOrThrow(id));
    }

    public SubSeriesDto toDto(SubSeries subSeries) {
        SeriesDto seriesDto = new SeriesDto(subSeries.getSeries().getId(), subSeries.getSeries().getName());
        return new SubSeriesDto(subSeries.getId(), subSeries.getName(), seriesDto);
    }

    private SubSeries findOrThrow(String id) {
        return subSeriesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubSeries not found"));
    }
}
