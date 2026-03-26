package com.bookshelf.book.controller;

import com.bookshelf.book.dto.CreateSubSeriesRequest;
import com.bookshelf.book.dto.SubSeriesDto;
import com.bookshelf.book.service.SubSeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sub-series")
@RequiredArgsConstructor
public class SubSeriesController {

    private final SubSeriesService subSeriesService;

    @GetMapping
    public List<SubSeriesDto> listSubSeries(@RequestParam(required = false) String seriesId) {
        if (seriesId != null) {
            return subSeriesService.listBySeriesId(seriesId);
        }
        return subSeriesService.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubSeriesDto createSubSeries(@Valid @RequestBody CreateSubSeriesRequest request) {
        return subSeriesService.create(request);
    }

    @GetMapping("/{id}")
    public SubSeriesDto getSubSeries(@PathVariable String id) {
        return subSeriesService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubSeries(@PathVariable String id) {
        subSeriesService.delete(id);
    }
}
