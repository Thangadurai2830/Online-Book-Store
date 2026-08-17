package com.bookstore.bookservice.controller;

import com.bookstore.bookservice.dto.BookRequestDto;
import com.bookstore.bookservice.dto.BookResponseDto;
import com.bookstore.bookservice.dto.PagedResponse;
import com.bookstore.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<PagedResponse<BookResponseDto>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Getting all books - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        PagedResponse<BookResponseDto> books = bookService.getAllBooks(page, size, sortBy, sortDir);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        log.info("Getting book by id: {}", id);
        BookResponseDto book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponseDto> getBookByIsbn(@PathVariable String isbn) {
        log.info("Getting book by ISBN: {}", isbn);
        BookResponseDto book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> createBook(@Valid @RequestBody BookRequestDto bookRequestDto) {
        log.info("Creating new book with title: {}", bookRequestDto.getTitle());
        BookResponseDto createdBook = bookService.createBook(bookRequestDto);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDto bookRequestDto) {
        log.info("Updating book with id: {}", id);
        BookResponseDto updatedBook = bookService.updateBook(id, bookRequestDto);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("Deleting book with id: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<BookResponseDto>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Searching books with query: {} - page: {}, size: {}", query, page, size);
        PagedResponse<BookResponseDto> books = bookService.searchBooks(query, page, size, sortBy, sortDir);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<PagedResponse<BookResponseDto>> getBooksByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Getting books by category: {} - page: {}, size: {}", category, page, size);
        PagedResponse<BookResponseDto> books = bookService.getBooksByCategory(category, page, size, sortBy, sortDir);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<PagedResponse<BookResponseDto>> getBooksByAuthor(
            @PathVariable String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Getting books by author: {} - page: {}, size: {}", author, page, size);
        PagedResponse<BookResponseDto> books = bookService.getBooksByAuthor(author, page, size, sortBy, sortDir);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/available")
    public ResponseEntity<PagedResponse<BookResponseDto>> getAvailableBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Getting available books - page: {}, size: {}", page, size);
        PagedResponse<BookResponseDto> books = bookService.getAvailableBooks(page, size, sortBy, sortDir);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        log.info("Getting all categories");
        List<String> categories = bookService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/authors")
    public ResponseEntity<List<String>> getAllAuthors() {
        log.info("Getting all authors");
        List<String> authors = bookService.getAllAuthors();
        return ResponseEntity.ok(authors);
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> updateBookStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        log.info("Updating stock for book id: {} to quantity: {}", id, quantity);
        BookResponseDto updatedBook = bookService.updateBookStock(id, quantity);
        return ResponseEntity.ok(updatedBook);
    }
}
