package com.bookstore.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "book-service")
public interface BookServiceClient {
    @GetMapping("/api/books/{bookId}")
    BookDto getBook(@PathVariable Long bookId, @RequestHeader("Authorization") String token);
}
