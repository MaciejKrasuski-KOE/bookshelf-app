package com.bookshelf.user.grpc;

import com.bookshelf.grpc.user.*;
import com.bookshelf.user.model.User;
import com.bookshelf.user.service.JwtService;
import com.bookshelf.user.service.UserService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            User user = userService.findById(request.getUserId());
            responseObserver.onNext(UserResponse.newBuilder()
                    .setUserId(user.getId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("getUser failed for id={}", request.getUserId(), e);
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void validateToken(ValidateTokenRequest request, StreamObserver<ValidateTokenResponse> responseObserver) {
        String token = request.getToken();
        boolean valid = jwtService.validateToken(token);
        ValidateTokenResponse.Builder builder = ValidateTokenResponse.newBuilder().setValid(valid);

        if (valid) {
            try {
                String username = jwtService.extractUsername(token);
                User user = userService.findByUsername(username);
                builder.setUserId(user.getId()).setUsername(user.getUsername());
            } catch (Exception e) {
                log.warn("Token valid but user lookup failed: {}", e.getMessage());
                builder.setValid(false);
            }
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
