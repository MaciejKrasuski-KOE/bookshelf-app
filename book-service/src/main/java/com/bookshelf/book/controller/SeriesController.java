package com.bookshelf.book.controller;

import com.bookshelf.book.dto.CreateSeriesRequest;
import com.bookshelf.book.dto.SeriesDto;
import com.bookshelf.book.service.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    @GetMapping
    public List<SeriesDto> listSeries() {
        return seriesService.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeriesDto createSeries(@Valid @RequestBody CreateSeriesRequest request) {
        return seriesService.create(request);
    }

    @GetMapping("/{id}")
    public SeriesDto getSeries(@PathVariable String id) {
        return seriesService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeries(@PathVariable String id) {
        seriesService.delete(id);
    }
}
