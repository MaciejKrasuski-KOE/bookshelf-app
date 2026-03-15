package com.bookshelf.review.grpc;

import com.bookshelf.grpc.review.*;
import com.bookshelf.review.service.ReviewService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class ReviewGrpcService extends ReviewServiceGrpc.ReviewServiceImplBase {

    private final ReviewService reviewService;

    @Override
    public void getReviewsByBook(GetReviewsByBookRequest request,
                                 StreamObserver<ReviewsResponse> responseObserver) {
        try {
            ReviewsResponse.Builder builder = ReviewsResponse.newBuilder();
            reviewService.getByBook(request.getBookId()).forEach(dto ->
                    builder.addReviews(toProto(dto)));
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getReviewsByUser(GetReviewsByUserRequest request,
                                 StreamObserver<ReviewsResponse> responseObserver) {
        try {
            ReviewsResponse.Builder builder = ReviewsResponse.newBuilder();
            reviewService.getByUser(request.getUserId()).forEach(dto ->
                    builder.addReviews(toProto(dto)));
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void createReview(CreateReviewRequest request,
                             StreamObserver<ReviewResponse> responseObserver) {
        try {
            var dto = reviewService.create(request.getUserId(),
                    new com.bookshelf.review.dto.CreateReviewRequest(
                            request.getBookId(), request.getRating(), request.getContent()));
            responseObserver.onNext(toProto(dto));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private ReviewResponse toProto(com.bookshelf.review.dto.ReviewDto dto) {
        return ReviewResponse.newBuilder()
                .setReviewId(dto.id())
                .setUserId(dto.userId())
                .setBookId(dto.bookId())
                .setRating(dto.rating())
                .setContent(dto.content() != null ? dto.content() : "")
                .setVerifiedReader(dto.verifiedReader())
                .setCreatedAt(dto.createdAt().toString())
                .build();
    }
}
