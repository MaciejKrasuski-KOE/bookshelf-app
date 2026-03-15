package com.bookshelf.review.service;

import com.bookshelf.review.dto.CreateReviewRequest;
import com.bookshelf.review.dto.ReviewDto;
import com.bookshelf.review.grpc.ShelfGrpcClient;
import com.bookshelf.review.model.Review;
import com.bookshelf.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ShelfGrpcClient shelfGrpcClient;

    public List<ReviewDto> getByBook(String bookId) {
        return reviewRepository.findByBookId(bookId).stream().map(this::toDto).toList();
    }

    public List<ReviewDto> getByUser(String userId) {
        return reviewRepository.findByUserId(userId).stream().map(this::toDto).toList();
    }

    @Transactional
    public ReviewDto create(String userId, CreateReviewRequest request) {
        if (reviewRepository.existsByUserIdAndBookId(userId, request.bookId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already reviewed this book");
        }
        boolean verified = shelfGrpcClient.isBookOnUserShelf(userId, request.bookId());
        Review review = Review.builder()
                .userId(userId)
                .bookId(request.bookId())
                .rating(request.rating())
                .content(request.content())
                .verifiedReader(verified)
                .build();
        return toDto(reviewRepository.save(review));
    }

    @Transactional
    public void delete(String reviewId, String userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        if (!review.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        reviewRepository.delete(review);
    }

    private ReviewDto toDto(Review r) {
        return new ReviewDto(r.getId(), r.getUserId(), r.getBookId(),
                r.getRating(), r.getContent(), r.isVerifiedReader(), r.getCreatedAt());
    }
}
