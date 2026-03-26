package com.bookshelf.book.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "sub_series")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubSeries {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
