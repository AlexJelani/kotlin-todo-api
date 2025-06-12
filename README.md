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
├── .github/             # CI/CD configuration
│   └── workflows/
│       ├── main.yml     # Application build and deployment workflow
│       └── terraform.yml # Infrastructure deployment workflow
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

## CI/CD Pipeline Setup

This project includes GitHub Actions workflows for CI/CD. The workflows are currently configured to run only on manual triggers until you complete the setup.

### Prerequisites for CI/CD

Before enabling automatic CI/CD pipeline runs, complete these steps:

1. **Set up GitHub Secrets**:
   Add these secrets in your GitHub repository settings (Settings > Secrets and variables > Actions):
   - `GCP_SA_KEY`: JSON key for a Google Cloud service account
   - `DB_HOST`: Your PostgreSQL host
   - `DB_PORT`: Your PostgreSQL port
   - `DB_NAME`: Your database name
   - `DB_USER`: Your database user
   - `DB_PASSWORD`: Your database password

2. **Create a Google Cloud Service Account**:
   - Go to GCP Console > IAM & Admin > Service Accounts
   - Create a new service account with these roles:
     - Cloud Run Admin
     - Storage Admin
     - Cloud Build Editor
     - Service Account User
   - Create and download a JSON key
   - Add this JSON key as the `GCP_SA_KEY` secret in GitHub

3. **Enable Required APIs** in your Google Cloud project:
   - Cloud Run API
   - Cloud Build API
   - Container Registry API

4. **Enable Automatic Workflow Triggers**:
   - Once all prerequisites are complete, uncomment the trigger sections in:
     - `.github/workflows/main.yml`
     - `.github/workflows/terraform.yml`
   - Commit and push these changes to enable automatic CI/CD

### Manual Workflow Execution

Until automatic triggers are enabled, you can manually run the workflows:
1. Go to the "Actions" tab in your GitHub repository
2. Select the workflow you want to run
3. Click "Run workflow" and select the branch
