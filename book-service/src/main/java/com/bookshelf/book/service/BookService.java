package com.bookshelf.book.service;

import com.bookshelf.book.dto.*;
import com.bookshelf.book.model.*;
import com.bookshelf.book.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final SeriesRepository seriesRepository;
    private final SubSeriesRepository subSeriesRepository;

    public List<BookDto> listAll() {
        return bookRepository.findAll().stream().map(this::toDto).toList();
    }

    public BookDto getById(String id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public BookDto create(String ownerId, CreateBookRequest request) {
        validateEshopUrl(request.getBookType(), request.getEshopUrl());
        Book book = Book.builder()
                .ownerId(ownerId)
                .title(request.getTitle())
                .originalTitle(request.getOriginalTitle())
                .authors(resolveAuthors(request.getAuthorIds()))
                .bookType(request.getBookType())
                .eshopUrl(request.getEshopUrl())
                .privateFileKey(request.getPrivateFileKey())
                .series(resolveSeries(request.getSeriesId()))
                .subSeries(resolveSubSeries(request.getSubSeriesId()))
                .seriesOrder(request.getSeriesOrder())
                .subSeriesOrder(request.getSubSeriesOrder())
                .build();
        return toDto(bookRepository.save(book));
    }

    @Transactional
    public BookDto update(String bookId, String userId, UpdateBookRequest request) {
        Book book = findOrThrow(bookId);
        authorizeOwner(book, userId);
        validateEshopUrl(request.getBookType(), request.getEshopUrl());
        book.setTitle(request.getTitle());
        book.setOriginalTitle(request.getOriginalTitle());
        book.setAuthors(resolveAuthors(request.getAuthorIds()));
        book.setBookType(request.getBookType());
        book.setEshopUrl(request.getEshopUrl());
        book.setPrivateFileKey(request.getPrivateFileKey());
        book.setSeries(resolveSeries(request.getSeriesId()));
        book.setSubSeries(resolveSubSeries(request.getSubSeriesId()));
        book.setSeriesOrder(request.getSeriesOrder());
        book.setSubSeriesOrder(request.getSubSeriesOrder());
        return toDto(bookRepository.save(book));
    }

    @Transactional
    public void delete(String bookId, String userId) {
        Book book = findOrThrow(bookId);
        authorizeOwner(book, userId);
        bookRepository.delete(book);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Book findOrThrow(String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    private void authorizeOwner(Book book, String userId) {
        if (!book.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    private void validateEshopUrl(BookType bookType, String eshopUrl) {
        if (eshopUrl != null && bookType != BookType.EBOOK) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "eshopUrl is only allowed for EBOOK type books");
        }
    }

    private List<Author> resolveAuthors(List<String> authorIds) {
        return authorIds.stream()
                .map(id -> authorRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found: " + id)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Series resolveSeries(String seriesId) {
        if (seriesId == null) {
            return null;
        }
        return seriesRepository.findById(seriesId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found"));
    }

    private SubSeries resolveSubSeries(String subSeriesId) {
        if (subSeriesId == null) {
            return null;
        }
        return subSeriesRepository.findById(subSeriesId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubSeries not found"));
    }

    public BookDto toDto(Book book) {
        SeriesDto seriesDto = book.getSeries() == null ? null
                : new SeriesDto(book.getSeries().getId(), book.getSeries().getName());
        SubSeriesDto subSeriesDto = book.getSubSeries() == null ? null
                : new SubSeriesDto(
                        book.getSubSeries().getId(),
                        book.getSubSeries().getName(),
                        new SeriesDto(book.getSubSeries().getSeries().getId(), book.getSubSeries().getSeries().getName()));

        return BookDto.builder()
                .id(book.getId())
                .ownerId(book.getOwnerId())
                .title(book.getTitle())
                .originalTitle(book.getOriginalTitle())
                .authors(book.getAuthors().stream()
                        .map(a -> new AuthorDto(a.getId(), a.getName()))
                        .toList())
                .bookType(book.getBookType())
                .eshopUrl(book.getEshopUrl())
                .privateFileKey(book.getPrivateFileKey())
                .series(seriesDto)
                .subSeries(subSeriesDto)
                .seriesOrder(book.getSeriesOrder())
                .subSeriesOrder(book.getSubSeriesOrder())
                .createdAt(book.getCreatedAt())
                .build();
    }
}
