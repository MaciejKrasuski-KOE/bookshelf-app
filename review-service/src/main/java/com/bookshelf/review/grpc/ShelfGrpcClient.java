package com.bookshelf.review.grpc;

import com.bookshelf.grpc.shelf.*;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShelfGrpcClient {

    @GrpcClient("shelf-service")
    private ShelfServiceGrpc.ShelfServiceBlockingStub shelfStub;

    /**
     * Returns true if the given bookId is present on any of the user's shelves.
     * Used to mark reviews as "verified reader".
     */
    public boolean isBookOnUserShelf(String userId, String bookId) {
        try {
            ShelvesResponse response = shelfStub.getShelvesByUser(
                    GetShelvesByUserRequest.newBuilder().setUserId(userId).build());
            return response.getShelvesList().stream()
                    .anyMatch(shelf -> shelf.getBookIdsList().contains(bookId));
        } catch (StatusRuntimeException e) {
            log.warn("Could not verify shelf membership for user={} book={}: {}", userId, bookId, e.getStatus());
            return false;
        }
    }
}
