# 📚 Library Tracker API
Library Tracker is a robust RESTful API built with **Spring Boot 3** and **Java 21**, designed to help users manage and
track their personal book collections. It features seamless integration with the **Open Library API** for automatic
metadata retrieval and follows enterprise-grade architectural standards.
## 🚀 Key Features
- **User Authentication:** Secure registration and login powered by JWT (JSON Web Tokens).
- **External API Integration:** Automatically fetch book details (Title, Authors, ISBN, Cover Image) using the Open
Library API.
- **Personal Collection Management:** Users can maintain a private list of books they own or want to read.
- **Reading Progress Tracking:** Manage book statuses (*UNREAD, READING, COMPLETED*) and save personalized notes for
each book.
- **Advanced Security Architecture:**
  - Centralized business logic validation through a dedicated `ValidationService`.
  - Custom Security Handlers (`AccessDeniedHandler` & `AuthenticationEntryPoint`) for consistent JSON error responses.
- **JPA Auditing:** Automatic tracking of creation and modification timestamps for all entities.
## 🛠️ Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.4.x
- **Security:** Spring Security & JWT (Stateless)
- **Database:** PostgreSQL
- **Persistence:** Spring Data JPA (Hibernate)
- **Mapping & Tooling:** Lombok & Java 21 Records
- **External Integration:** RestTemplate (Open Library API)
## 📋 Prerequisites
Ensure you have the following installed:
- **JDK 21**
- **Maven 3.9+**
- **PostgreSQL**
## ⚙️ Getting Started
**Clone the repository:**
git clone https://github.com/yourusername/library-tracker.git

**Configure the Database:**
- Update `src/main/resources/application.properties` with your PostgreSQL credentials:
  - spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
  - spring.datasource.username=your_username

## 🛣️ API Endpoints Overview
### Authentication
- `POST /api/v1/auth/register` - Create a new user account.
- `POST /api/v1/auth/login` - Authenticate and receive a JWT token.
### Administrative Actions (Admin Only)
- `GET /api/v1/external-books/{isbn}` - Fetch book metadata from Open Library.
- `POST /api/v1/books` - Save a new book to the system's global database.

### User Collection
- `GET /api/v1/my-books` - Retrieve the authenticated user's book collection.
- `POST /api/v1/my-books/{bookId}` - Add a book from the system to the user's collection.
- `PUT /api/v1/my-books/{myBookId}` - Update reading status or personal notes.                                          
- `DELETE /api/v1/my-books/{myBookId}` - Remove a book from the user's collection.

## 🏗️ Architecture & Best Practices
- **UUID Primary Keys:** All entities use UUIDs instead of auto-incremented integers for enhanced security and
scalability.
- **Separation of Concerns:** Business rules are strictly separated into a `ValidationService`, keeping the primary
Services lean and focused on data orchestration.
- **DRY Mapping:** DTO mapping is centralized to ensure consistency across different service layers.
- **Stateless Security:** Implements a fully stateless JWT-based security filter chain.