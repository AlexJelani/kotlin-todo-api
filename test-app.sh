#!/bin/bash

# Build the Docker image
docker build -t kotlin-todo-api-test .

# Run the container with the PORT environment variable set
# Use port 8082 on the host to avoid conflicts
docker run -e PORT=8080 -p 8082:8080 kotlin-todo-api-test

# This will run the container in the foreground so you can see the logs
