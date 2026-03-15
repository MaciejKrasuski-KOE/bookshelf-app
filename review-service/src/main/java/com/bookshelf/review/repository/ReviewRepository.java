package com.bookshelf.review.repository;

import com.bookshelf.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByBookId(String bookId);
    List<Review> findByUserId(String userId);
    Optional<Review> findByUserIdAndBookId(String userId, String bookId);
    boolean existsByUserIdAndBookId(String userId, String bookId);
}
