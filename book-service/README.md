# 📚 Book Service

The Book Service is a core microservice in the Online Bookstore application responsible for managing the book catalog, inventory, and search functionality.

## 🎯 Purpose

This service provides comprehensive book management capabilities including:

- Book catalog management (CRUD operations)
- Advanced search and filtering
- Category and author management
- Stock tracking and management
- Pagination and sorting

## 🚀 Features

### Public Features (No Authentication Required)

- **Browse Books**: Get paginated list of all books
- **Book Details**: View detailed information about specific books
- **Search**: Full-text search across title, author, and description
- **Filter by Category**: Browse books by specific categories
- **Filter by Author**: Browse books by specific authors
- **Available Books**: View only books with stock > 0
- **Categories List**: Get all available book categories
- **Authors List**: Get all available authors

### Admin Features (ADMIN Role Required)

- **Add Books**: Create new book entries
- **Update Books**: Modify existing book information
- **Delete Books**: Remove books from catalog
- **Stock Management**: Update stock quantities

## 🛠️ Technology Stack

- **Framework**: Spring Boot 2.7.17
- **Security**: Spring Security with JWT
- **Database**: H2 (In-memory)
- **ORM**: Spring Data JPA
- **Service Discovery**: Netflix Eureka Client
- **Documentation**: Comprehensive logging
- **Testing**: JUnit 5 + Mockito
- **Code Generation**: Lombok

## 📡 API Endpoints

### Base URL: `http://localhost:8083/api/books`

#### Public Endpoints

| Method | Endpoint               | Description          | Parameters                          |
| ------ | ---------------------- | -------------------- | ----------------------------------- |
| GET    | `/`                    | Get all books        | `page`, `size`, `sortBy`, `sortDir` |
| GET    | `/{id}`                | Get book by ID       | Path: `id`                          |
| GET    | `/isbn/{isbn}`         | Get book by ISBN     | Path: `isbn`                        |
| GET    | `/search`              | Search books         | Query: `query`, pagination params   |
| GET    | `/category/{category}` | Books by category    | Path: `category`, pagination params |
| GET    | `/author/{author}`     | Books by author      | Path: `author`, pagination params   |
| GET    | `/available`           | Available books only | Pagination params                   |
| GET    | `/categories`          | All categories       | None                                |
| GET    | `/authors`             | All authors          | None                                |

#### Admin Endpoints (Require `ROLE_ADMIN`)

| Method | Endpoint      | Description     | Body                 |
| ------ | ------------- | --------------- | -------------------- |
| POST   | `/`           | Create new book | BookRequestDto       |
| PUT    | `/{id}`       | Update book     | BookRequestDto       |
| DELETE | `/{id}`       | Delete book     | None                 |
| PATCH  | `/{id}/stock` | Update stock    | `quantity` parameter |

### Example Requests

#### Get All Books

```http
GET /api/books?page=0&size=10&sortBy=title&sortDir=asc
```

#### Search Books

```http
GET /api/books/search?query=Spring&page=0&size=5
```

#### Create Book (Admin)

```http
POST /api/books
Content-Type: application/json
Authorization: Bearer <jwt-token>

{
  "title": "Advanced Spring Boot",
  "author": "John Developer",
  "isbn": "978-1234567890",
  "description": "Comprehensive guide to Spring Boot",
  "price": 49.99,
  "stockQuantity": 100,
  "category": "Technology",
  "publisher": "Tech Publications",
  "publicationYear": 2023,
  "language": "English",
  "pages": 450,
  "imageUrl": "https://example.com/book-cover.jpg"
}
```

## 🗄️ Database Schema

### Books Table

```sql
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(17) UNIQUE NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    category VARCHAR(100),
    publisher VARCHAR(100),
    publication_year INTEGER,
    language VARCHAR(50),
    pages INTEGER,
    image_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Constraints

- `isbn` must be unique
- `title`, `author`, `price`, `stock_quantity` are required
- `price` must be positive
- `stock_quantity` must be non-negative

## 📊 Sample Data

The service initializes with 5 sample books:

1. **The Spring Boot Guide** - John Smith (Technology)
2. **Microservices Architecture** - Jane Doe (Technology)
3. **Java Concurrency in Practice** - Brian Goetz (Programming)
4. **Clean Code** - Robert C. Martin (Programming)
5. **Refactoring** - Martin Fowler (Software Engineering)

## 🔒 Security

### Authentication

- Public endpoints: No authentication required
- Admin endpoints: Require valid JWT token with `ROLE_ADMIN`

### Authorization

```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<BookResponseDto> createBook(@RequestBody BookRequestDto request)
```

### JWT Token Format

```
Authorization: Bearer <jwt-token>
```

## 🧪 Testing

### Running Tests

```bash
cd book-service
mvn clean test
```

### Test Coverage

- **Total Tests**: 12
- **Service Layer**: 11 tests
- **Application Context**: 1 test
- **Coverage**: Unit tests for all business logic

### Test Categories

1. **CRUD Operations**: Create, Read, Update, Delete
2. **Search Functionality**: Text search, category filtering
3. **Error Handling**: Not found exceptions, validation errors
4. **Business Logic**: Stock management, data validation

## 🚀 Running the Service

### Prerequisites

- Java 11+
- Maven 3.6+
- Eureka Server running on port 8761

### Standalone

```bash
cd book-service
mvn clean install
mvn spring-boot:run
```

### With JAR

```bash
cd book-service
mvn clean package
java -jar target/book-service-0.0.1-SNAPSHOT.jar
```

### Development Mode

```bash
cd book-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## ⚙️ Configuration

### Application Properties

```properties
# Server Configuration
server.port=8083
spring.application.name=book-service

# Database Configuration
spring.datasource.url=jdbc:h2:mem:bookdb
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.instance.prefer-ip-address=true

# Security Configuration
jwt.secret=bookstore-secret-key
jwt.expiration=86400

# Logging Configuration
logging.level.com.bookstore.bookservice=DEBUG
```

### Environment Variables

```bash
export EUREKA_SERVER_URL=http://localhost:8761/eureka
export JWT_SECRET=your-secret-key
export DB_URL=jdbc:h2:mem:bookdb
```

## 🐛 Troubleshooting

### Common Issues

1. **Service Not Starting**

   ```bash
   # Check if port 8083 is available
   netstat -ano | findstr :8083

   # Kill process if needed
   taskkill /F /PID <process_id>
   ```

2. **Eureka Registration Failed**

   - Ensure Eureka server is running
   - Check `eureka.client.service-url.defaultZone` property
   - Verify network connectivity

3. **Database Issues**

   ```bash
   # Access H2 console
   http://localhost:8083/h2-console
   # JDBC URL: jdbc:h2:mem:bookdb
   # Username: sa
   # Password: (empty)
   ```

4. **JWT Authentication Errors**
   - Verify token format: `Bearer <token>`
   - Check token expiration
   - Ensure User Service is running for token validation

## 📈 Performance

### Optimization Features

- **Pagination**: All list endpoints support pagination
- **Lazy Loading**: JPA relationships optimized
- **Caching**: Entity caching for frequently accessed data
- **Indexing**: Database indexes on ISBN and title

### Monitoring

- **Health Check**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Info**: `/actuator/info`

## 🔄 Integration

### Service Dependencies

- **Eureka Server**: Service registration and discovery
- **User Service**: JWT token validation (for admin operations)
- **API Gateway**: Request routing and load balancing

### Inter-Service Communication

```java
// Example: Validate user permissions
@Autowired
private UserServiceClient userServiceClient;

public boolean validateAdminUser(String token) {
    return userServiceClient.hasAdminRole(token);
}
```

## 🚀 Deployment

### Docker (Future)

```dockerfile
FROM openjdk:11-jre-slim
COPY target/book-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Production Considerations

- Replace H2 with PostgreSQL/MySQL
- Add connection pooling
- Implement caching layer (Redis)
- Add monitoring and alerts
- Configure log aggregation

## 📝 Development Guidelines

### Code Standards

- Use Lombok for reducing boilerplate
- Implement proper exception handling
- Add comprehensive logging
- Follow REST API conventions
- Write unit tests for all public methods

### Git Workflow

- Create feature branches for new functionality
- Write meaningful commit messages
- Keep commits focused and atomic
- Review code before merging

## 🔮 Future Enhancements

- [ ] Add book reviews and ratings
- [ ] Implement book recommendations
- [ ] Add image upload functionality
- [ ] Implement advanced search with Elasticsearch
- [ ] Add book categories hierarchy
- [ ] Implement real-time inventory updates
- [ ] Add book availability notifications
- [ ] Implement book reservations

## 📞 Support

For issues or questions:

1. Check the troubleshooting section
2. Review application logs
3. Check Eureka dashboard for service status
4. Verify database connectivity

---

**Book Service v0.0.1-SNAPSHOT** - Online Bookstore Microservices
