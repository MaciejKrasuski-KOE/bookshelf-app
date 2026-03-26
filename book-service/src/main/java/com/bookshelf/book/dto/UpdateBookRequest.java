package com.bookshelf.book.dto;

import com.bookshelf.book.model.BookType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookRequest {
    @NotBlank
    private String title;
    private String originalTitle;
    @NotEmpty
    private List<String> authorIds;
    @NotNull
    private BookType bookType;
    private String eshopUrl;
    private String privateFileKey;
    private String seriesId;
    private String subSeriesId;
    private Integer seriesOrder;
    private Integer subSeriesOrder;
}
