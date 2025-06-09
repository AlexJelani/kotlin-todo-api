FROM gradle:7.6.1-jdk17 AS build

WORKDIR /app
COPY . /app/
RUN gradle build --no-daemon

FROM openjdk:17-slim

WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Expose the port the app runs on
EXPOSE 9000

# Command to run the application
CMD ["java", "-jar", "/app/app.jar"]
