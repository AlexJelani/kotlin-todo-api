# Google Cloud Run Deployment with Terraform

This directory contains Terraform configuration to deploy the Kotlin Todo API to Google Cloud Run, connecting to your existing PostgreSQL database in OCI.

## Prerequisites

1. [Google Cloud SDK](https://cloud.google.com/sdk/docs/install) installed and configured
2. [Terraform](https://www.terraform.io/downloads.html) installed (v1.0.0+)
3. Docker installed and running
4. A Google Cloud Platform project with billing enabled
5. Your existing PostgreSQL database in OCI must be accessible from the internet

## Setup

1. Create a `terraform.tfvars` file based on the example:

```bash
cp terraform.tfvars.example terraform.tfvars
```

2. Edit `terraform.tfvars` with your specific values:
   - `project_id`: Your GCP project ID
   - `region`: The GCP region to deploy to
   - `db_password`: Your PostgreSQL database password

## Network Configuration

Ensure your OCI PostgreSQL database allows connections from Google Cloud Run:

1. Configure your OCI security lists to allow inbound traffic on port 5432 from Google Cloud IP ranges
2. Make sure your PostgreSQL configuration allows remote connections (pg_hba.conf)

## Deployment

Initialize Terraform:

```bash
terraform init
```

Plan the deployment:

```bash
terraform plan
```

Apply the configuration:

```bash
terraform apply
```

## Accessing the API

After deployment, Terraform will output the URL of your Cloud Run service. You can access your API at this URL:

```
https://kotlin-todo-api-[hash].a.run.app
```

## Cleanup

To remove all resources:

```bash
terraform destroy
```

## Notes

- The first deployment may take several minutes as it builds and pushes the Docker image
- Subsequent deployments will be faster if the Dockerfile and source code haven't changed
- Environment variables containing sensitive information are marked as sensitive in Terraform
