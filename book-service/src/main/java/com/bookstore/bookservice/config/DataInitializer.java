package com.bookstore.bookservice.config;

import com.bookstore.bookservice.model.Book;
import com.bookstore.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        if (bookRepository.count() == 0) {
            log.info("Initializing sample book data...");

            // Create sample books
            Book book1 = new Book();
            book1.setTitle("The Spring Boot Guide");
            book1.setAuthor("John Smith");
            book1.setIsbn("978-1234567890");
            book1.setDescription("A comprehensive guide to Spring Boot development");
            book1.setPrice(new BigDecimal("49.99"));
            book1.setStockQuantity(100);
            book1.setCategory("Technology");
            book1.setPublisher("Tech Publications");
            book1.setPublicationYear(2023);
            book1.setLanguage("English");
            book1.setPages(450);
            book1.setImageUrl("https://example.com/spring-boot-guide.jpg");

            Book book2 = new Book();
            book2.setTitle("Microservices Architecture");
            book2.setAuthor("Jane Doe");
            book2.setIsbn("978-0987654321");
            book2.setDescription("Building scalable microservices with Spring Cloud");
            book2.setPrice(new BigDecimal("59.99"));
            book2.setStockQuantity(75);
            book2.setCategory("Technology");
            book2.setPublisher("Cloud Press");
            book2.setPublicationYear(2023);
            book2.setLanguage("English");
            book2.setPages(520);
            book2.setImageUrl("https://example.com/microservices-arch.jpg");

            Book book3 = new Book();
            book3.setTitle("Java Concurrency in Practice");
            book3.setAuthor("Brian Goetz");
            book3.setIsbn("978-0321349606");
            book3.setDescription("Essential guide to Java concurrency programming");
            book3.setPrice(new BigDecimal("45.95"));
            book3.setStockQuantity(50);
            book3.setCategory("Programming");
            book3.setPublisher("Addison-Wesley");
            book3.setPublicationYear(2006);
            book3.setLanguage("English");
            book3.setPages(384);
            book3.setImageUrl("https://example.com/java-concurrency.jpg");

            Book book4 = new Book();
            book4.setTitle("Clean Code");
            book4.setAuthor("Robert C. Martin");
            book4.setIsbn("978-0132350884");
            book4.setDescription("A handbook of agile software craftsmanship");
            book4.setPrice(new BigDecimal("42.99"));
            book4.setStockQuantity(80);
            book4.setCategory("Programming");
            book4.setPublisher("Prentice Hall");
            book4.setPublicationYear(2008);
            book4.setLanguage("English");
            book4.setPages(464);
            book4.setImageUrl("https://example.com/clean-code.jpg");

            Book book5 = new Book();
            book5.setTitle("Design Patterns");
            book5.setAuthor("Gang of Four");
            book5.setIsbn("978-0201633612");
            book5.setDescription("Elements of reusable object-oriented software");
            book5.setPrice(new BigDecimal("54.95"));
            book5.setStockQuantity(60);
            book5.setCategory("Software Engineering");
            book5.setPublisher("Addison-Wesley");
            book5.setPublicationYear(1994);
            book5.setLanguage("English");
            book5.setPages(395);
            book5.setImageUrl("https://example.com/design-patterns.jpg");

            bookRepository.save(book1);
            bookRepository.save(book2);
            bookRepository.save(book3);
            bookRepository.save(book4);
            bookRepository.save(book5);

            log.info("Sample book data initialized successfully!");
        } else {
            log.info("Book data already exists, skipping initialization.");
        }
    }
}
