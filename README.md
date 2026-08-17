# 📚 Online Bookstore - Microservices Architecture

A comprehensive online bookstore application built using **Spring Boot microservices architecture** with service discovery, API gateway, and distributed security.

## 🏗️ Architecture Overview

This project demonstrates a modern microservices architecture with the following components:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   API Gateway   │    │ Service Registry│
│   (Future)      │◄───┤   Port: 8082    │◄───┤ Eureka Server   │
│                 │    │                 │    │   Port: 8761    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
        ▼                       ▼                       ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ User Service │    │ Book Service │    │Order Service │
│  Port: 8081  │    │  Port: 8083  │    │  Port: 8084  │
│              │    │              │    │              │
│ - Auth/JWT   │    │ - Catalog    │    │ - Orders     │
│ - Users      │    │ - Inventory  │    │ - Payments   │
│ - Roles      │    │ - Search     │    │ - History    │
└──────────────┘    └──────────────┘    └──────────────┘
        │                       │                       │
        ▼                       ▼                       ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   H2 DB      │    │   H2 DB      │    │   H2 DB      │
│   (Users)    │    │   (Books)    │    │   (Orders)   │
└──────────────┘    └──────────────┘    └──────────────┘
```

## 🚀 Services

### 1. **Eureka Server** (Service Registry)

- **Port**: 8761
- **Purpose**: Service discovery and registration
- **URL**: http://localhost:8761

### 2. **API Gateway**

- **Port**: 8082
- **Purpose**: Request routing, load balancing, authentication
- **Technology**: Spring Cloud Gateway
- **Security**: JWT-based authentication

### 3. **User Service**

- **Port**: 8081
- **Purpose**: User management and authentication
- **Features**:
  - User registration and login
  - JWT token generation and validation
  - Role-based access control (USER, ADMIN)
  - Password encryption
- **Database**: H2 in-memory database

### 4. **Book Service**

- **Port**: 8083
- **Purpose**: Book catalog management
- **Features**:
  - CRUD operations for books
  - Advanced search functionality
  - Category and author filtering
  - Stock management
  - Pagination and sorting
- **Security**: Role-based endpoints (Public browsing, Admin management)
- **Database**: H2 in-memory database

### 5. **Order Service**

- **Port**: 8084
- **Purpose**: Order processing and management
- **Features**: Order creation, tracking, and history
- **Database**: H2 in-memory database

## 🛠️ Technology Stack

| Component             | Technology            | Version  |
| --------------------- | --------------------- | -------- |
| **Framework**         | Spring Boot           | 2.7.17   |
| **Service Discovery** | Netflix Eureka        | 2021.0.8 |
| **API Gateway**       | Spring Cloud Gateway  | 2021.0.8 |
| **Security**          | Spring Security + JWT | 5.7.x    |
| **Database**          | H2 (In-Memory)        | Runtime  |
| **ORM**               | Spring Data JPA       | 2.7.x    |
| **Build Tool**        | Maven                 | 3.8+     |
| **Java Version**      | Java 11               | 11.0.2+  |
| **Code Generation**   | Lombok                | 1.18.30  |

## 📋 Prerequisites

- **Java 11** or higher
- **Maven 3.6+**
- **Git**
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Online-Book-Store
```

### 2. Start Services in Order

#### Step 1: Start Eureka Server (Service Registry)

```bash
cd eureka-server
mvn spring-boot:run
```

Wait for startup, then verify at: http://localhost:8761

#### Step 2: Start API Gateway

```bash
cd api-gateway
mvn clean install
mvn spring-boot:run
```

#### Step 3: Start Microservices

Open new terminals for each service:

```bash
# User Service
cd user-service
mvn clean install
mvn spring-boot:run

# Book Service
cd book-service
mvn clean install
mvn spring-boot:run

# Order Service
cd order-service
mvn clean install
mvn spring-boot:run
```

### 3. Verify All Services

Check Eureka Dashboard: http://localhost:8761

You should see all services registered:

- API-GATEWAY
- USER-SERVICE
- BOOK-SERVICE
- ORDER-SERVICE

## 📖 API Documentation

### Book Service Endpoints (Port 8083)

#### Public Endpoints (No Authentication)

```http
GET    /api/books                          # Get all books (paginated)
GET    /api/books/{id}                     # Get book by ID
GET    /api/books/isbn/{isbn}              # Get book by ISBN
GET    /api/books/search?query={term}      # Search books
GET    /api/books/category/{category}      # Get books by category
GET    /api/books/author/{author}          # Get books by author
GET    /api/books/available               # Get available books
GET    /api/books/categories              # Get all categories
GET    /api/books/authors                 # Get all authors
```

#### Admin Endpoints (Require ADMIN role)

```http
POST   /api/books                         # Create new book
PUT    /api/books/{id}                    # Update book
DELETE /api/books/{id}                    # Delete book
PATCH  /api/books/{id}/stock              # Update stock quantity
```

### User Service Endpoints (Port 8081)

```http
POST   /api/auth/register                 # User registration
POST   /api/auth/login                    # User login
GET    /api/users/profile                 # Get user profile (authenticated)
PUT    /api/users/profile                 # Update user profile (authenticated)
```

### Through API Gateway (Port 8082)

All services can be accessed through the API Gateway:

```http
GET    http://localhost:8082/api/books    # Routes to Book Service
POST   http://localhost:8082/api/auth/login # Routes to User Service
```

## 🧪 Testing

### Run All Tests

```bash
# Test all services
mvn clean test

# Test specific service
cd book-service
mvn clean test
```

### Test Results

- **Book Service**: 12/12 tests passing
- **User Service**: 2/2 tests passing
- **Total**: 14/14 tests passing ✅

## 🔒 Security

### Authentication Flow

1. User registers/logs in via User Service
2. JWT token is generated and returned
3. Client includes token in Authorization header: `Bearer <token>`
4. API Gateway validates token and routes requests
5. Services verify token and authorize based on roles

### Roles

- **USER**: Can browse books, view details, search
- **ADMIN**: Full access including book management

## 🗄️ Database Schema

### Book Service (H2 Database)

```sql
Table: books
- id (BIGINT, Primary Key)
- title (VARCHAR(255), NOT NULL)
- author (VARCHAR(255), NOT NULL)
- isbn (VARCHAR(17), UNIQUE, NOT NULL)
- description (VARCHAR(1000))
- price (DECIMAL(10,2), NOT NULL)
- stock_quantity (INTEGER, NOT NULL)
- category (VARCHAR(100))
- publisher (VARCHAR(100))
- publication_year (INTEGER)
- language (VARCHAR(50))
- pages (INTEGER)
- image_url (VARCHAR(255))
- created_at (TIMESTAMP, NOT NULL)
- updated_at (TIMESTAMP)
```

### User Service (H2 Database)

```sql
Table: users
- id (BIGINT, Primary Key)
- username (VARCHAR(255), UNIQUE, NOT NULL)
- email (VARCHAR(255), UNIQUE, NOT NULL)
- password (VARCHAR(255), NOT NULL)
- first_name (VARCHAR(255))
- last_name (VARCHAR(255))
- phone (VARCHAR(255))
- address (VARCHAR(255))
- role (VARCHAR(255), NOT NULL)
```

## 📊 Sample Data

The Book Service comes pre-loaded with sample data:

- 5 books across Technology, Programming, and Software Engineering categories
- Authors: John Smith, Jane Doe, Brian Goetz, Robert C. Martin, Martin Fowler

## 🐛 Troubleshooting

### Common Issues

1. **Port Already in Use**

   ```bash
   # Kill process using port (Windows)
   netstat -ano | findstr :8083
   taskkill /F /PID <process_id>
   ```

2. **Service Not Registering with Eureka**

   - Ensure Eureka server is running first
   - Check application.properties for correct eureka.client.service-url

3. **JWT Authentication Errors**

   - Verify token format: `Bearer <token>`
   - Check token expiration
   - Ensure User Service is running

4. **Database Connection Issues**
   - H2 databases are in-memory and reset on restart
   - Check H2 console: http://localhost:8083/h2-console

## 🔧 Configuration

### Environment Variables

Create `.env` file or set system variables:

```bash
EUREKA_SERVER_URL=http://localhost:8761/eureka
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400
```

### Profiles

- `default`: Development with H2 database
- `test`: Test environment with H2
- `prod`: Production configuration (future)

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Submit Pull Request

## 📝 Development Guidelines

### Code Style

- Use Lombok for reducing boilerplate
- Follow Spring Boot best practices
- Implement proper exception handling
- Add comprehensive logging
- Write unit tests for all services

### Git Workflow

- Use meaningful commit messages
- Create feature branches
- Review code before merging
- Keep commits atomic and focused

## 🚀 Future Enhancements

- [ ] Add frontend (React/Angular)
- [ ] Implement order processing workflow
- [ ] Add payment gateway integration
- [ ] Implement caching (Redis)
- [ ] Add monitoring (Actuator, Micrometer)
- [ ] Database migration to PostgreSQL
- [ ] Docker containerization
- [ ] Kubernetes deployment
- [ ] CI/CD pipeline
- [ ] API rate limiting
- [ ] Email notifications
- [ ] Advanced search with Elasticsearch

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Development Team** - _Initial work_ - Online Bookstore Project

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Netflix OSS for Eureka service discovery
- Spring Cloud team for microservices tools

---

**Happy Coding!** 🎉
