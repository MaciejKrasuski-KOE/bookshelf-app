package com.bookshelf.book.dto;

import com.bookshelf.book.model.BookType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private String id;
    private String ownerId;
    private String title;
    private String originalTitle;
    private List<AuthorDto> authors;
    private BookType bookType;
    private String eshopUrl;
    private String privateFileKey;
    private SeriesDto series;
    private SubSeriesDto subSeries;
    private Integer seriesOrder;
    private Integer subSeriesOrder;
    private LocalDateTime createdAt;
}
