package com.bookshelf.shelf.repository;

import com.bookshelf.shelf.model.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, String> {
    List<Shelf> findByUserId(String userId);
    boolean existsByUserIdAndName(String userId, String name);
}
