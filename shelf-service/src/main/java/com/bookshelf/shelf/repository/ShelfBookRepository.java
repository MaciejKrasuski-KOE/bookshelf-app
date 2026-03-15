package com.bookshelf.shelf.repository;

import com.bookshelf.shelf.model.ShelfBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShelfBookRepository extends JpaRepository<ShelfBook, String> {
    Optional<ShelfBook> findByShelfIdAndBookId(String shelfId, String bookId);
    boolean existsByShelfIdAndBookId(String shelfId, String bookId);
}
