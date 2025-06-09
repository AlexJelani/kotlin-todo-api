# Todo API

A simple RESTful API for managing to-do tasks, built with Kotlin, Ktor, and PostgreSQL.

## Technologies Used

- Kotlin
- Ktor (Web framework)
- Exposed (SQL framework)
- PostgreSQL (Database)
- HikariCP (Connection pooling)
- Kotlinx Serialization (JSON serialization)

## Project Structure

```
todo-api/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/
│   │   │       └── todoapi/
│   │   │           ├── config/
│   │   │           │   └── DatabaseConfig.kt
│   │   │           ├── controllers/
│   │   │           │   └── TaskController.kt
│   │   │           ├── models/
│   │   │           │   └── Task.kt
│   │   │           ├── repositories/
│   │   │           │   └── TaskRepository.kt
│   │   │           ├── services/
│   │   │           │   └── TaskService.kt
│   │   │           └── Application.kt
│   │   └── resources/
│   │       ├── application.conf
│   │       └── logback.xml
│   └── test/
└── build.gradle.kts
```

## API Endpoints

- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get a specific task by ID
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update an existing task
- `DELETE /api/tasks/{id}` - Delete a task

## Running the Application

1. Make sure you have JDK 17+ installed
2. Clone the repository
3. Run the application:
   ```
   ./gradlew run
   ```
4. The API will be available at `http://localhost:9000`

## Database Configuration

The application is configured to connect to a remote PostgreSQL database with the following details:

- Host: 131.186.56.105
- Port: 5432
- Database: postgres_db
- User: postgres
- Password: postgres_password

## Sample API Usage

### Create a Task

```bash
curl -X POST http://localhost:9000/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Complete project", "description": "Finish the todo API project", "completed": false}'
```

### Get All Tasks

```bash
curl http://localhost:9000/api/tasks
```

### Get Task by ID

```bash
curl http://localhost:9000/api/tasks/1
```

### Update a Task

```bash
curl -X PUT http://localhost:9000/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Complete project", "description": "Finish the todo API project", "completed": true}'
```

### Delete a Task

```bash
curl -X DELETE http://localhost:9000/api/tasks/1
```
