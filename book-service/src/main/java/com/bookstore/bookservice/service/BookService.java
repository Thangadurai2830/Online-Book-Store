package com.bookstore.bookservice.service;

import com.bookstore.bookservice.dto.BookRequestDto;
import com.bookstore.bookservice.dto.BookResponseDto;
import com.bookstore.bookservice.dto.PagedResponse;
import com.bookstore.bookservice.exception.BookNotFoundException;
import com.bookstore.bookservice.exception.DuplicateIsbnException;
import com.bookstore.bookservice.model.Book;
import com.bookstore.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public PagedResponse<BookResponseDto> getAllBooks(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> bookPage = bookRepository.findAll(pageable);

        return createPagedResponse(bookPage);
    }

    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        return convertToResponseDto(book);
    }

    public BookResponseDto getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
        return convertToResponseDto(book);
    }

    public BookResponseDto createBook(BookRequestDto bookRequestDto) {
        log.info("Creating new book with ISBN: {}", bookRequestDto.getIsbn());

        if (bookRepository.findByIsbn(bookRequestDto.getIsbn()).isPresent()) {
            throw new DuplicateIsbnException("Book with ISBN " + bookRequestDto.getIsbn() + " already exists");
        }

        Book book = convertToEntity(bookRequestDto);
        Book savedBook = bookRepository.save(book);

        log.info("Successfully created book with id: {}", savedBook.getId());
        return convertToResponseDto(savedBook);
    }

    public BookResponseDto updateBook(Long id, BookRequestDto bookRequestDto) {
        log.info("Updating book with id: {}", id);

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        // Check if ISBN is being changed and if it already exists
        if (!existingBook.getIsbn().equals(bookRequestDto.getIsbn())) {
            Optional<Book> bookWithSameIsbn = bookRepository.findByIsbn(bookRequestDto.getIsbn());
            if (bookWithSameIsbn.isPresent() && !bookWithSameIsbn.get().getId().equals(id)) {
                throw new DuplicateIsbnException("Book with ISBN " + bookRequestDto.getIsbn() + " already exists");
            }
        }

        updateBookFromDto(existingBook, bookRequestDto);
        Book updatedBook = bookRepository.save(existingBook);

        log.info("Successfully updated book with id: {}", updatedBook.getId());
        return convertToResponseDto(updatedBook);
    }

    public void deleteBook(Long id) {
        log.info("Deleting book with id: {}", id);

        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with id: " + id);
        }

        bookRepository.deleteById(id);
        log.info("Successfully deleted book with id: {}", id);
    }

    public PagedResponse<BookResponseDto> searchBooks(String searchTerm, int page, int size, String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> bookPage = bookRepository.searchBooks(searchTerm, pageable);

        return createPagedResponse(bookPage);
    }

    public PagedResponse<BookResponseDto> getBooksByCategory(String category, int page, int size, String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> bookPage = bookRepository.findByCategory(category, pageable);

        return createPagedResponse(bookPage);
    }

    public PagedResponse<BookResponseDto> getBooksByAuthor(String author, int page, int size, String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> bookPage = bookRepository.findByAuthor(author, pageable);

        return createPagedResponse(bookPage);
    }

    public PagedResponse<BookResponseDto> getAvailableBooks(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> bookPage = bookRepository.findAvailableBooks(pageable);

        return createPagedResponse(bookPage);
    }

    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }

    public List<String> getAllAuthors() {
        return bookRepository.findAllAuthors();
    }

    public BookResponseDto updateBookStock(Long id, Integer quantity) {
        log.info("Updating stock for book id: {} with quantity: {}", id, quantity);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        book.setStockQuantity(quantity);
        Book updatedBook = bookRepository.save(book);

        log.info("Successfully updated stock for book id: {}", id);
        return convertToResponseDto(updatedBook);
    }

    // Helper methods
    private PagedResponse<BookResponseDto> createPagedResponse(Page<Book> bookPage) {
        List<BookResponseDto> content = bookPage.getContent().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isFirst(),
                bookPage.isLast());
    }

    private BookResponseDto convertToResponseDto(Book book) {
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getDescription(),
                book.getPrice(),
                book.getStockQuantity(),
                book.getCategory(),
                book.getPublisher(),
                book.getPublicationYear(),
                book.getLanguage(),
                book.getPages(),
                book.getImageUrl(),
                book.getCreatedAt(),
                book.getUpdatedAt());
    }

    private Book convertToEntity(BookRequestDto dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        book.setPrice(dto.getPrice());
        book.setStockQuantity(dto.getStockQuantity());
        book.setCategory(dto.getCategory());
        book.setPublisher(dto.getPublisher());
        book.setPublicationYear(dto.getPublicationYear());
        book.setLanguage(dto.getLanguage());
        book.setPages(dto.getPages());
        book.setImageUrl(dto.getImageUrl());
        return book;
    }

    private void updateBookFromDto(Book book, BookRequestDto dto) {
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        book.setPrice(dto.getPrice());
        book.setStockQuantity(dto.getStockQuantity());
        book.setCategory(dto.getCategory());
        book.setPublisher(dto.getPublisher());
        book.setPublicationYear(dto.getPublicationYear());
        book.setLanguage(dto.getLanguage());
        book.setPages(dto.getPages());
        book.setImageUrl(dto.getImageUrl());
    }
}
