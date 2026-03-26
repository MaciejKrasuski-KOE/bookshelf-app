package com.bookshelf.book.service;

import com.bookshelf.book.dto.CreateSeriesRequest;
import com.bookshelf.book.dto.SeriesDto;
import com.bookshelf.book.model.Series;
import com.bookshelf.book.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;

    public List<SeriesDto> listAll() {
        return seriesRepository.findAll().stream().map(this::toDto).toList();
    }

    public SeriesDto getById(String id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public SeriesDto create(CreateSeriesRequest request) {
        Series series = Series.builder().name(request.name()).build();
        return toDto(seriesRepository.save(series));
    }

    @Transactional
    public void delete(String id) {
        seriesRepository.delete(findOrThrow(id));
    }

    public SeriesDto toDto(Series series) {
        return new SeriesDto(series.getId(), series.getName());
    }

    private Series findOrThrow(String id) {
        return seriesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found"));
    }
}
