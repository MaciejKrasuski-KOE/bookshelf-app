package com.bookshelf.shelf.grpc;

import com.bookshelf.grpc.shelf.*;
import com.bookshelf.shelf.model.Shelf;
import com.bookshelf.shelf.model.ShelfBook;
import com.bookshelf.shelf.service.ShelfService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class ShelfGrpcService extends ShelfServiceGrpc.ShelfServiceImplBase {

    private final ShelfService shelfService;

    @Override
    public void getShelf(GetShelfRequest request, StreamObserver<ShelfResponse> responseObserver) {
        try {
            Shelf shelf = shelfService.findById(request.getShelfId());
            responseObserver.onNext(toProto(shelf));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("getShelf failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getShelvesByUser(GetShelvesByUserRequest request,
                                 StreamObserver<ShelvesResponse> responseObserver) {
        try {
            ShelvesResponse.Builder builder = ShelvesResponse.newBuilder();
            shelfService.getShelvesByUser(request.getUserId()).forEach(dto ->
                    builder.addShelves(ShelfResponse.newBuilder()
                            .setShelfId(dto.id())
                            .setUserId(dto.userId())
                            .setName(dto.name())
                            .addAllBookIds(dto.bookIds())
                            .build())
            );
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void addBookToShelf(AddBookToShelfRequest request, StreamObserver<ShelfResponse> responseObserver) {
        try {
            // gRPC callers provide owning userId via metadata in production; here we trust the shelf owner
            Shelf shelf = shelfService.findById(request.getShelfId());
            var dto = shelfService.addBook(request.getShelfId(), shelf.getUserId(), request.getBookId());
            responseObserver.onNext(ShelfResponse.newBuilder()
                    .setShelfId(dto.id())
                    .setUserId(dto.userId())
                    .setName(dto.name())
                    .addAllBookIds(dto.bookIds())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private ShelfResponse toProto(Shelf shelf) {
        return ShelfResponse.newBuilder()
                .setShelfId(shelf.getId())
                .setUserId(shelf.getUserId())
                .setName(shelf.getName())
                .addAllBookIds(shelf.getBooks().stream().map(ShelfBook::getBookId).toList())
                .build();
    }
}
