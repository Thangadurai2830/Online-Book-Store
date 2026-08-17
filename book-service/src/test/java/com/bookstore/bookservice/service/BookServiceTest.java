package com.bookstore.bookservice.service;

import com.bookstore.bookservice.dto.BookRequestDto;
import com.bookstore.bookservice.dto.BookResponseDto;
import com.bookstore.bookservice.dto.PagedResponse;
import com.bookstore.bookservice.exception.BookNotFoundException;
import com.bookstore.bookservice.exception.DuplicateIsbnException;
import com.bookstore.bookservice.model.Book;
import com.bookstore.bookservice.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookRequestDto testBookRequestDto;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("978-1234567890");
        testBook.setDescription("Test Description");
        testBook.setPrice(new BigDecimal("29.99"));
        testBook.setStockQuantity(100);
        testBook.setCategory("Test Category");
        testBook.setPublisher("Test Publisher");
        testBook.setPublicationYear(2023);
        testBook.setLanguage("English");
        testBook.setPages(300);
        testBook.setImageUrl("https://example.com/test.jpg");
        testBook.setCreatedAt(LocalDateTime.now());
        testBook.setUpdatedAt(LocalDateTime.now());

        testBookRequestDto = new BookRequestDto();
        testBookRequestDto.setTitle("Test Book");
        testBookRequestDto.setAuthor("Test Author");
        testBookRequestDto.setIsbn("978-1234567890");
        testBookRequestDto.setDescription("Test Description");
        testBookRequestDto.setPrice(new BigDecimal("29.99"));
        testBookRequestDto.setStockQuantity(100);
        testBookRequestDto.setCategory("Test Category");
        testBookRequestDto.setPublisher("Test Publisher");
        testBookRequestDto.setPublicationYear(2023);
        testBookRequestDto.setLanguage("English");
        testBookRequestDto.setPages(300);
        testBookRequestDto.setImageUrl("https://example.com/test.jpg");
    }

    @Test
    void getAllBooks_ShouldReturnPagedResponse() {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 10, Sort.by("id")), 1);
        when(bookRepository.findAll(any(PageRequest.class))).thenReturn(bookPage);

        // Act
        PagedResponse<BookResponseDto> result = bookService.getAllBooks(0, 10, "id", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
        verify(bookRepository).findAll(any(PageRequest.class));
    }

    @Test
    void getBookById_WhenExists_ShouldReturnBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // Act
        BookResponseDto result = bookService.getBookById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));
        verify(bookRepository).findById(1L);
    }

    @Test
    void createBook_WhenIsbnNotExists_ShouldCreateBook() {
        // Arrange
        when(bookRepository.findByIsbn(testBookRequestDto.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        BookResponseDto result = bookService.createBook(testBookRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).findByIsbn(testBookRequestDto.getIsbn());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_WhenIsbnExists_ShouldThrowException() {
        // Arrange
        when(bookRepository.findByIsbn(testBookRequestDto.getIsbn())).thenReturn(Optional.of(testBook));

        // Act & Assert
        assertThrows(DuplicateIsbnException.class, () -> bookService.createBook(testBookRequestDto));
        verify(bookRepository).findByIsbn(testBookRequestDto.getIsbn());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_WhenExists_ShouldUpdateBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        BookResponseDto result = bookService.updateBook(1L, testBookRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(1L, testBookRequestDto));
        verify(bookRepository).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_WhenExists_ShouldDeleteBook() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(true);

        // Act
        bookService.deleteBook(1L);

        // Assert
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository).existsById(1L);
        verify(bookRepository, never()).deleteById(1L);
    }

    @Test
    void searchBooks_ShouldReturnMatchingBooks() {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 10, Sort.by("id")), 1);
        when(bookRepository.searchBooks(eq("test"), any(PageRequest.class))).thenReturn(bookPage);

        // Act
        PagedResponse<BookResponseDto> result = bookService.searchBooks("test", 0, 10, "id", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookRepository).searchBooks(eq("test"), any(PageRequest.class));
    }

    @Test
    void updateBookStock_WhenExists_ShouldUpdateStock() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        BookResponseDto result = bookService.updateBookStock(1L, 50);

        // Assert
        assertNotNull(result);
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }
}
