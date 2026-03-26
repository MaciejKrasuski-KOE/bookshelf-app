package com.bookshelf.book.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "owner_id", nullable = false, length = 36)
    private String ownerId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "original_title", length = 255)
    private String originalTitle;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @Builder.Default
    private List<Author> authors = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "book_type", nullable = false, length = 10)
    private BookType bookType;

    @Column(name = "eshop_url", length = 500)
    private String eshopUrl;

    @Column(name = "private_file_key", length = 500)
    private String privateFileKey;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "series_id")
    private Series series;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sub_series_id")
    private SubSeries subSeries;

    @Column(name = "series_order")
    private Integer seriesOrder;

    @Column(name = "sub_series_order")
    private Integer subSeriesOrder;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
