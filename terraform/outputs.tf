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
