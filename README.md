# Rating System Application

A Spring Boot application for managing user ratings and comments for game-related content.

## Features

### User Management

- User registration and authentication
- Role-based access control (Admin/User)
- User activation via email confirmation
- User profile management
- User rankings based on ratings

### Comment System

- Create, read, update, and delete comments
- Comment moderation (approval/rejection) by admins
- Rating system (1-5 stars)
- Anonymous commenting with optional registration

### Game Objects

- Game listing and management
- Associate games with users
- Filter ratings by game

### Rating System

- Automatic rating calculation
- Average rating computation
- Total ratings tracking
- User ranking based on ratings
- Rating history

## Project Structure

```
rating-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/leverx/ratingapp/
│   │   │       ├── config/             # Application configuration
│   │   │       │   ├── aop/           # Aspect-oriented programming configs
│   │   │       │   │   ├── AdminLoggingAspect
│   │   │       │   │   ├── AuthLoggingAspect
│   │   │       │   │   ├── BaseLoggingAspect
│   │   │       │   │   ├── CommentLoggingAspect
│   │   │       │   │   ├── GameObjectLoggingAspect
│   │   │       │   │   └── UserLoggingAspect
│   │   │       │   ├── init/          # Initialization configs
│   │   │       │   │   ├── AdminInitializationConfig
│   │   │       │   │   └── EnvLoader
│   │   │       │   ├── redis/         # Redis configuration
│   │   │       │   └── security/      # Security configurations
│   │   │       ├── controllers/        # REST API endpoints
│   │   │       │   ├── AdminController
│   │   │       │   ├── AuthController
│   │   │       │   ├── CommentController
│   │   │       │   ├── GameObjectController
│   │   │       │   └── UserController
│   │   │       ├── dtos/              # Data Transfer Objects
│   │   │       │   ├── auth/          # Authentication DTOs
│   │   │       │   ├── comments/      # Comment DTOs
│   │   │       │   ├── error/         # Error response DTOs
│   │   │       │   ├── gameobject/    # Game object DTOs
│   │   │       │   └── user/          # User DTOs
│   │   │       ├── models/            # Entity models
│   │   │       │   ├── entities/      # JPA entities
│   │   │       │   │   ├── Comment
│   │   │       │   │   ├── GameObject
│   │   │       │   │   ├── SellerRating
│   │   │       │   │   └── User
│   │   │       │   └── enums/         # Enumerations
│   │   │       │       ├── Role
│   │   │       │       └── Status
│   │   │       ├── exceptions/         # Custom exceptions
│   │   │       ├── repositories/       # Data access layer
│   │   │       │   ├── redis/         # Redis repositories
│   │   │       │   ├── token/         # Token repositories
│   │   │       │   ├── CommentRepository
│   │   │       │   ├── GameObjectRepository
│   │   │       │   ├── SellerRatingRepository
│   │   │       │   └── UserRepository
│   │   │       └── services/          # Business logic
│   │   │           ├── auth/          # Authentication services
│   │   │           │   ├── jwt/       # JWT services
│   │   │           │   └── token/     # Token services
│   │   │           ├── comment/       # Comment management
│   │   │           ├── email/         # Email services
│   │   │           ├── gameobject/    # Game object services
│   │   │           ├── pendingcomment/ # Pending comment services
│   │   │           ├── rating/        # Rating calculations
│   │   │           └── user/          # User operations
│   │   └── resources/
│   │       ├── application.properties  # Application configuration
│   │       └── templates/             # Email templates
│   └── test/
        └── java/
            └── org/leverx/ratingapp/
                ├── unit/              # Unit tests
                │   ├── CommentServiceUnitTests
                │   ├── GameObjectServiceUnitTests
                │   ├── RatingCalculationServiceUnitTests
                │   └── UserServiceUnitTests
                └── integration/       # Integration tests
                    ├── AuthControllerIntegrationTests  
                    ├── CommentControllerIntegrationTests
                    └── UserControllerIntegrationTests
```
### Running with Docker (Recommended)
1. Switch to the containerized branch:
   ```bash
   git checkout containerized
   ```
2. Run the application using Docker Compose:
   ```bash
   docker-compose up
   ```
   This will start the application, PostgreSQL, and Redis containers.
### Running with SAP BTP (Hightly recommended)
1. Switch to the containerized branch:
   ```bash
   git checkout deploy
   ```
2. Build the application using Maven:
   ```bash
    chmod +x mvnw && ./mvnw clean package -Dmaven.test.skip=true -Dspring.config.location=src/main/resources/application-cloud.properties
   ```
3. Send application to SAP BTP Cloud:
   ```bash
    cf push
   ```
   This will start the application, PostgreSQL, and Redis containers will be alredy on SAP BTP.
## API Endpoints

### Authentication

- `POST /auth/register` - Register new user
    - Body: `{firstName, lastName, email, password}`
- `POST /auth/authenticate` - Login user
    - Body: `{email, password}`
- `GET /auth/confirm?token={token}` - Confirm email registration (User can confirm using email)
    - Query: `token` - Email confirmation token

### Users

- `GET /users` - Get all users
    - Users: Can see only users with active status
    - Admins: Can see all users, even other admins
- `GET /users/{id}` - Get user by ID
    - Path: `id` - User's unique identifier
- `GET /users/rating` - Get user rankings
    - Query: `gameName` - Filter by game title (optional)
    - Query: `limit` - Limit number of results (optional)
- `GET /admin/users/inactive` - Get inactive users (Admin only)
- `POST /admin/users/{seller_id}/comments/{comment_id}` - Approve/reject comment (Admin only)

### Comments
- `POST /users/{seller_id}/comments` - Create comment for seller
    - Path: `seller_id` - Seller's unique identifier
    - Body: `{message, grade}`
    - Returns: Created comment with status "CREATED"

- `POST /users/{seller_id}/comments/optional-seller` - Create comment with optional seller registration
    - Path: `seller_id` - Seller's unique identifier
    - Body: `{message, grade, firstName, lastName, email, password}`
    - Returns: Comment pending status if seller needs registration

- `GET /users/{seller_id}/comments` - Get seller's comments
    - Path: `seller_id` - Seller's unique identifier
    - Users: Can see only approved comments and their own
    - Admins: Can see all comments

- `GET /users/{seller_id}/comments/{comment_id}` - Get specific comment
    - Path: `seller_id` - Seller's unique identifier
    - Path: `comment_id` - Comment's unique identifier
    - Returns: Comment details if visible to user

- `PUT /users/{seller_id}/comments/{comment_id}` - Update comment
    - Path: `seller_id` - Seller's unique identifier
    - Path: `comment_id` - Comment's unique identifier
    - Body: `{message, grade}`
    - Access: Comment author only

- `DELETE /users/{seller_id}/comments/{comment_id}` - Delete comment
    - Path: `seller_id` - Seller's unique identifier
    - Path: `comment_id` - Comment's unique identifier
    - Access: Comment author or admin
    - Returns: 202 Accepted with deletion status

- `POST /admin/users/{seller_id}/comments/{comment_id}` - Approve/reject comment
    - Path: `seller_id` - Seller's unique identifier
    - Path: `comment_id` - Comment's unique identifier
    - Query: `confirm` - true to approve, false to reject
    - Access: Admin only
    - Returns: Updated comment status

### Game Objects

- `POST /users/{user_id}/games` - Create game object
- `GET /users/{user_id}/games` - Get user's games
- `PUT /users/{user_id}/games/{game_id}` - Update game
- `DELETE /users/{user_id}/games/{game_id}` - Delete game

## Technologies

- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JUnit 5
- Mockito
- Maven

## Getting Started

1. Clone the repository
2. Configure database connection in `application.properties`
3. Run `mvn clean install`
4. Start the application using `mvn spring-boot:run`

## Testing

The application includes both unit and integration tests:

```bash
# Run all tests
mvn test

# Run unit tests only
mvn test -Dtest=*UnitTests

# Run integration tests only
mvn test -Dtest=*IntegrationTest
```

## Security

- JWT-based authentication
- Password encryption using BCrypt
- Role-based access control
- Email verification for new accounts

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request
