package com.bookstore.user_service;

import com.bookstore.user_service.dto.AuthResponse;
import com.bookstore.user_service.dto.RegisterRequest;
import com.bookstore.user_service.model.User;
import com.bookstore.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testUserRegistration() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhone("1234567890");
        request.setAddress("123 Main St, City");
        request.setRole(User.Role.ROLE_USER);

        // Act
        AuthResponse response = userService.register(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getToken());

        // Verify user is saved
        User user = userService.getUserByUsername("john");
        assertNotNull(user);
        assertEquals("john@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
    }
}
