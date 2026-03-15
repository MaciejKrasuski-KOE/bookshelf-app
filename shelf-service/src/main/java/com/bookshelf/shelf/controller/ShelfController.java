package com.bookshelf.shelf.controller;

import com.bookshelf.shelf.dto.AddBookRequest;
import com.bookshelf.shelf.dto.CreateShelfRequest;
import com.bookshelf.shelf.dto.ShelfDto;
import com.bookshelf.shelf.service.ShelfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelves")
@RequiredArgsConstructor
public class ShelfController {

    private final ShelfService shelfService;

    @GetMapping
    public List<ShelfDto> listShelves(@AuthenticationPrincipal String userId) {
        return shelfService.getShelvesByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShelfDto createShelf(@AuthenticationPrincipal String userId,
                                @Valid @RequestBody CreateShelfRequest request) {
        return shelfService.createShelf(userId, request);
    }

    @GetMapping("/{id}")
    public ShelfDto getShelf(@PathVariable String id,
                             @AuthenticationPrincipal String userId) {
        return shelfService.getShelf(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShelf(@PathVariable String id,
                            @AuthenticationPrincipal String userId) {
        shelfService.deleteShelf(id, userId);
    }

    @PostMapping("/{id}/books")
    public ShelfDto addBook(@PathVariable String id,
                            @AuthenticationPrincipal String userId,
                            @Valid @RequestBody AddBookRequest request) {
        return shelfService.addBook(id, userId, request.bookId());
    }

    @DeleteMapping("/{id}/books/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBook(@PathVariable String id,
                           @PathVariable String bookId,
                           @AuthenticationPrincipal String userId) {
        shelfService.removeBook(id, userId, bookId);
    }
}
