# Library Tracker - Project Instructions

## Project Summary
A casual REST API for tracking personal reading collections and book statuses, integrated with the Google Books API for metadata retrieval.

## Core Tech Stack
*Language:* Java 21
*Framework:* Spring Boot 3.x
*Security:* Spring Security & JWT (Stateless)
*Database:* JPA/Hibernate (PostgreSQL) with JPA Auditing
*External Integration:* RestTemplate (Google Books API)
*Documentation:* Springdoc OpenAPI

## Architectural Mandates
1. *Entity Design:*
- Extend AbstractAuditingEntity for all domain entities.
- Use @Getter and @Setter instead of @Data to avoid JPA issues.
- Use @Builder, but always include @NoArgsConstructor and @AllArgsConstructor.
- Primary Keys must be UUID.

2. *Security & Context:*
- Use @CurrentUser annotation with a custom HandlerMethodArgumentResolver.
- Protect sensitive endpoints with @PreAuthorize.
- Implement CustomAccessDeniedHandler and CustomAuthenticationEntryPoint for standard JSON error responses.

3. *Data Transfer (DTOs):*
- Use Java 21 *Records* for read-only DTOs and API responses.
- Wrap all API responses in a consistent WebResponse<T> envelope.

4. *Validation:*
- Use @Valid in Controllers for input validation.
- Use a dedicated ValidationService for complex business logic validation in the Service layer.

5. *External API:*
- Use RestTemplate for external API calls.
- Map external JSON responses to internal Records before saving to the database.

## Naming Conventions
- Packages: com.rizalamar.librarytracker
- Sub-packages: domain, repository, service, controller, dto, security, config, exception.
- Database Tables: snake_case (e.g., reading_logs).
- Variables & Methods: camelCase.