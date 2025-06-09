# Todo API

A simple RESTful API for managing to-do tasks, built with Kotlin, Ktor, and PostgreSQL.

## Technologies Used

- Kotlin
- Ktor (Web framework)
- Exposed (SQL framework)
- PostgreSQL (Database)
- HikariCP (Connection pooling)
- Kotlinx Serialization (JSON serialization)
- Docker (Containerization)
- Terraform (Infrastructure as Code)
- Google Cloud Run (Optional deployment target)

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
├── terraform/           # GCP deployment configuration
│   ├── main.tf
│   ├── variables.tf
│   ├── outputs.tf
│   └── versions.tf
├── .env.example
├── docker-compose.yml
├── Dockerfile
└── build.gradle.kts
```

## API Endpoints

- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get a specific task by ID
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update an existing task
- `DELETE /api/tasks/{id}` - Delete a task

## Running the Application

### Using Docker (Recommended for Local Development)

1. Make sure you have Docker and Docker Compose installed
2. Create a `.env` file based on `.env.example` with your database credentials
3. Build and start the containers:
   ```
   docker-compose up -d
   ```
4. The API will be available at `http://localhost:9000`

### Using Gradle

1. Make sure you have JDK 17+ installed
2. Clone the repository
3. Configure your database connection in `src/main/resources/application.conf` or set environment variables
4. Run the application:
   ```
   ./gradlew run
   ```
5. The API will be available at `http://localhost:9000`

### Deploying to Google Cloud Run with Terraform

#### Prerequisites

1. [Google Cloud SDK](https://cloud.google.com/sdk/docs/install) installed and configured
2. [Terraform](https://www.terraform.io/downloads.html) installed (v1.0.0+)
3. Docker installed and running
4. A Google Cloud Platform project with billing enabled
5. Your PostgreSQL database must be accessible from the internet

#### Deployment Steps

1. Navigate to the terraform directory:
   ```bash
   cd terraform
   ```

2. Create a `terraform.tfvars` file based on the example:
   ```bash
   cp terraform.tfvars.example terraform.tfvars
   ```

3. Edit `terraform.tfvars` with your specific values:
   - `project_id`: Your GCP project ID
   - `region`: The GCP region to deploy to
   - `db_password`: Your PostgreSQL database password

4. Initialize and apply Terraform:
   ```bash
   terraform init
   terraform apply
   ```

5. After deployment, Terraform will output the URL of your Cloud Run service.

6. To remove all resources:
   ```bash
   terraform destroy
   ```

## Database Configuration

The application uses environment variables for database configuration:

```
DB_HOST=your_database_host
DB_PORT=your_database_port
DB_NAME=your_database_name
DB_USER=your_database_user
DB_PASSWORD=your_database_password
```

These can be set in a `.env` file for Docker or as system environment variables when running with Gradle.

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

## Notes on Cloud Deployment

- When deploying to Google Cloud Run, ensure your database allows connections from Google Cloud IP ranges
- The first deployment may take several minutes as it builds and pushes the Docker image
- Environment variables containing sensitive information are marked as sensitive in Terraform
- Cloud Run automatically scales based on traffic and you only pay for actual usage
