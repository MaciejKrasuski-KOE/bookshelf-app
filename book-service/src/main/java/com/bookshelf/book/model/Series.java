package com.bookshelf.book.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "series")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Series {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
