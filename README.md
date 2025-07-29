# Task Management API

A Spring Boot REST API for managing projects and tasks with AWS Cognito authentication.

## Features

- **Authentication**: AWS Cognito integration for user management
- **Project Management**: Create and manage projects
- **Task Management**: CRUD operations for tasks with status tracking
- **Admin Functions**: Administrative operations
- **Database**: PostgreSQL with JPA/Hibernate

## Tech Stack

- Java 21
- Spring Boot 3.5.4
- PostgreSQL
- AWS Cognito
- Maven
- Docker

## Getting Started

### Prerequisites

- Java 21
- Maven
- Docker & Docker Compose
- AWS Cognito User Pool

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd task-management-api
   ```

2. **Start the database**
   ```bash
   docker-compose up -d
   ```

3. **Configure the application**
   - Copy `src/main/resources/application-example.properties` to `application.properties`
   - Update database credentials and AWS Cognito settings

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8080`

### Database Access

- **PostgreSQL**: `localhost:5432` (postgres/admin)
- **Adminer**: `http://localhost:8888` (for database management)

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signin` | User sign in |
| POST | `/api/auth/force-password-change` | Force password change |

### Projects
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects/all` | List all user projects |
| GET | `/api/projects` | List user projects (paginated) |
| GET | `/api/projects/{projectId}` | Get project by ID |
| POST | `/api/projects` | Create new project |
| PUT | `/api/projects/{projectId}` | Update project |
| DELETE | `/api/projects/{projectId}` | Delete project |

### Tasks
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks/all` | List all user tasks |
| GET | `/api/tasks` | List user tasks (paginated) |
| GET | `/api/tasks/all?projectId={id}` | List tasks by project |
| GET | `/api/tasks?projectId={id}` | List tasks by project (paginated) |
| GET | `/api/tasks/{taskId}` | Get task by ID |
| POST | `/api/tasks` | Create new task |
| PUT | `/api/tasks/{taskId}` | Update task |
| DELETE | `/api/tasks/{taskId}` | Delete task |

### Admin (Requires admin role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| DELETE | `/api/users/{id}` | Delete user |

**Note**: All endpoints except authentication require a valid JWT token in the `Authorization: Bearer <token>` header.

## Configuration

Key configuration properties in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

# AWS Cognito
aws.cognito.user-pool-id=your-user-pool-id
aws.cognito.client-id=your-client-id
aws.cognito.region=your-region
```

## AWS Cognito Setup

### Key Points:

- **User Pool**: Create a Cognito User Pool in AWS Console
- **App Client**: Configure an app client with appropriate auth flows
- **Authentication**: API uses JWT tokens from Cognito for authentication
- **User Management**: Users are managed through Cognito (sign up, sign in, password reset)
- **Token Validation**: API validates JWT tokens on protected endpoints

### Required Cognito Configuration:

1. **User Pool Settings**:
   - Enable username/email sign-in
   - Configure password policies as needed
   - Set up any required user attributes

2. **App Client Settings**:
   - Enable "ALLOW_USER_PASSWORD_AUTH" flow
   - Configure callback/logout URLs if using hosted UI

3. **IAM Permissions**: Ensure your application has appropriate AWS credentials to validate tokens

### Authentication Flow:

1. User signs in through Cognito (returns JWT tokens)
2. Client includes `Authorization: Bearer <access_token>` in API requests
3. API validates the token and extracts user information
4. Use `@CurrentUser` annotation in controllers to access authenticated user details
