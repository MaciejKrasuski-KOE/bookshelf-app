package com.bookshelf.book.repository;

import com.bookshelf.book.model.SubSeries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubSeriesRepository extends JpaRepository<SubSeries, String> {
    List<SubSeries> findBySeriesId(String seriesId);
}
