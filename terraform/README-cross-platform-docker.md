# Deploying Docker Containers from ARM to x86 Platforms

This README explains how to avoid architecture mismatch issues when building Docker containers on ARM-based machines (like M1/M2/M3 Macs) and deploying them to x86-64 platforms like Google Cloud Run.

## The Problem

When you build a Docker image on an ARM-based machine without specifying the target platform, the resulting image contains ARM binaries that cannot run on x86-64 servers. This results in errors like:

```
terminated: Application failed to start: failed to load /usr/local/openjdk-17/bin/java: exec format error
```

This is a common issue when developing on Apple Silicon (M1/M2/M3) Macs and deploying to cloud platforms.

## Solution: Cross-Platform Docker Builds

### Option 1: Using the `--platform` flag with Docker Build

When building your Docker image, specify the target platform:

```bash
docker build --platform linux/amd64 -t your-image-name:tag .
docker push your-image-name:tag
```

### Option 2: Specifying Platform in Dockerfile

You can also specify the platform in your Dockerfile using the `FROM --platform` directive:

```dockerfile
FROM --platform=linux/amd64 gradle:7.6.1-jdk17 AS build

WORKDIR /app
COPY . /app/
RUN gradle build --no-daemon

FROM --platform=linux/amd64 openjdk:17-slim

WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
```

## Terraform Integration

If you're using Terraform to build and deploy your containers, modify your build command to include the platform flag:

```hcl
resource "null_resource" "build_and_push_image" {
  triggers = {
    docker_file = filesha256("${path.module}/Dockerfile")
    source_code = filesha256("${path.module}/src/main/kotlin/Application.kt")
  }

  provisioner "local-exec" {
    command = <<-EOT
      gcloud auth configure-docker --quiet
      docker build --platform linux/amd64 -t gcr.io/${var.project_id}/your-app:latest ${path.module}
      docker push gcr.io/${var.project_id}/your-app:latest
    EOT
  }
}
```

## Testing Cross-Platform Compatibility

To verify your image will work on the target platform:

```bash
# Pull and run your image with platform emulation
docker run --platform linux/amd64 your-image-name:tag
```

## Common Errors and Solutions

| Error | Solution |
|-------|----------|
| `exec format error` | Use `--platform linux/amd64` when building |
| Slow builds | Building x86 images on ARM requires emulation and will be slower |
| Runtime performance issues | Consider using CI/CD pipelines on x86 machines for production builds |

## Best Practices

1. **Always specify the target platform** when building Docker images on ARM machines
2. **Use CI/CD pipelines** on x86 machines for production builds when possible
3. **Test your containers locally** with the target platform flag before deploying
4. **Consider multi-architecture images** with Docker buildx for production applications

## Example Workflow

```bash
# Development workflow on M1/M2/M3 Mac
# 1. Build for local testing (native ARM)
docker build -t myapp:dev .
docker run -p 8080:8080 myapp:dev

# 2. Build for deployment (x86-64)
docker build --platform linux/amd64 -t gcr.io/my-project/myapp:latest .
docker push gcr.io/my-project/myapp:latest

# 3. Deploy to Google Cloud Run
gcloud run deploy myapp --image gcr.io/my-project/myapp:latest --platform managed
```

By following these practices, you'll avoid architecture mismatch issues when deploying containers from ARM development machines to x86-64 cloud platforms.
