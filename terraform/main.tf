provider "google" {
  project = var.project_id
  region  = var.region
}

# Enable required APIs
resource "google_project_service" "cloud_run_api" {
  service            = "run.googleapis.com"
  disable_on_destroy = false
}

resource "google_project_service" "container_registry_api" {
  service            = "containerregistry.googleapis.com"
  disable_on_destroy = false
}

# Create a service account for Cloud Run
resource "google_service_account" "cloud_run_service_account" {
  account_id   = "kotlin-todo-api-sa"
  display_name = "Service Account for Kotlin Todo API"
}

# Build and push Docker image to Container Registry
resource "null_resource" "build_and_push_image" {
  triggers = {
    docker_file = filesha256("${path.module}/../Dockerfile")
    source_code = filesha256("${path.module}/../src/main/kotlin/com/todoapi/Application.kt")
  }

  provisioner "local-exec" {
    command = <<-EOT
      gcloud auth configure-docker --quiet
      docker build -t gcr.io/${var.project_id}/kotlin-todo-api:latest ${path.module}/..
      docker push gcr.io/${var.project_id}/kotlin-todo-api:latest
    EOT
  }

  depends_on = [google_project_service.container_registry_api]
}

# Deploy to Cloud Run
resource "google_cloud_run_service" "kotlin_todo_api" {
  name     = "kotlin-todo-api"
  location = var.region

  template {
    spec {
      containers {
        image = "gcr.io/${var.project_id}/kotlin-todo-api:latest"
        
        env {
          name  = "DB_HOST"
          value = var.db_host
        }
        
        env {
          name  = "DB_PORT"
          value = var.db_port
        }
        
        env {
          name  = "DB_NAME"
          value = var.db_name
        }
        
        env {
          name  = "DB_USER"
          value = var.db_user
        }
        
        env {
          name  = "DB_PASSWORD"
          value = var.db_password
        }
        
        env {
          name  = "JDBC_DATABASE_URL"
          value = "jdbc:postgresql://${var.db_host}:${var.db_port}/${var.db_name}"
        }
        
        env {
          name  = "JDBC_DATABASE_USERNAME"
          value = var.db_user
        }
        
        env {
          name  = "JDBC_DATABASE_PASSWORD"
          value = var.db_password
        }
        
        resources {
          limits = {
            cpu    = "1000m"
            memory = "512Mi"
          }
        }
      }
      
      service_account_name = google_service_account.cloud_run_service_account.email
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  depends_on = [
    google_project_service.cloud_run_api,
    null_resource.build_and_push_image
  ]
}

# Make the Cloud Run service publicly accessible
resource "google_cloud_run_service_iam_member" "public_access" {
  service  = google_cloud_run_service.kotlin_todo_api.name
  location = google_cloud_run_service.kotlin_todo_api.location
  role     = "roles/run.invoker"
  member   = "allUsers"
}

# Output the service URL
output "service_url" {
  value = google_cloud_run_service.kotlin_todo_api.status[0].url
}
