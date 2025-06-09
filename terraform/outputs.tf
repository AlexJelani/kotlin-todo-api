output "service_url" {
  description = "The URL of the deployed Cloud Run service"
  value       = google_cloud_run_service.kotlin_todo_api.status[0].url
}

output "project_id" {
  description = "The GCP project ID"
  value       = var.project_id
}

output "region" {
  description = "The GCP region"
  value       = var.region
}

output "database_connection" {
  description = "Database connection information"
  value       = "Connected to PostgreSQL at ${var.db_host}:${var.db_port}/${var.db_name}"
  sensitive   = false
}
