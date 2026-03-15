package com.bookshelf.review.controller;

import com.bookshelf.review.dto.CreateReviewRequest;
import com.bookshelf.review.dto.ReviewDto;
import com.bookshelf.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/book/{bookId}")
    public List<ReviewDto> getByBook(@PathVariable String bookId) {
        return reviewService.getByBook(bookId);
    }

    @GetMapping("/user/{userId}")
    public List<ReviewDto> getByUser(@PathVariable String userId) {
        return reviewService.getByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto create(@AuthenticationPrincipal String userId,
                            @Valid @RequestBody CreateReviewRequest request) {
        return reviewService.create(userId, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id,
                       @AuthenticationPrincipal String userId) {
        reviewService.delete(id, userId);
    }
}
