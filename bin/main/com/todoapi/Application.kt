package com.todoapi

import com.todoapi.config.DatabaseConfig
import com.todoapi.controllers.taskRoutes
import com.todoapi.repositories.TaskRepository
import com.todoapi.services.TaskService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import io.ktor.server.config.*

fun main() {
    // Read port from environment variable or use default 8080 (for Cloud Run compatibility)
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    
    println("Starting server on port $port")
    
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Initialize database with environment variables or config
    val dbHost = System.getenv("DB_HOST") ?: "localhost"
    val dbPort = System.getenv("DB_PORT") ?: "5432"
    val dbName = System.getenv("DB_NAME") ?: "postgres_db"
    val dbUser = System.getenv("JDBC_DATABASE_USERNAME") ?: "postgres"
    val dbPassword = System.getenv("JDBC_DATABASE_PASSWORD") ?: "postgres_password"
    
    println("Connecting to database at $dbHost:$dbPort/$dbName")
    
    val dbConfig = MapApplicationConfig().apply {
        put("database.host", dbHost)
        put("database.port", dbPort)
        put("database.name", dbName)
        put("database.user", dbUser)
        put("database.password", dbPassword)
    }
    
    try {
        DatabaseConfig.init(dbConfig)
    } catch (e: Exception) {
        println("Warning: Database connection failed: ${e.message}")
        // Continue application startup even if database connection fails
    }
    
    // Configure JSON serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    
    // Configure CORS
    install(CORS) {
        anyHost()
        allowHeader(io.ktor.http.HttpHeaders.ContentType)
        allowHeader(io.ktor.http.HttpHeaders.Authorization)
        allowMethod(io.ktor.http.HttpMethod.Options)
        allowMethod(io.ktor.http.HttpMethod.Get)
        allowMethod(io.ktor.http.HttpMethod.Post)
        allowMethod(io.ktor.http.HttpMethod.Put)
        allowMethod(io.ktor.http.HttpMethod.Delete)
    }
    
    // Add a health check endpoint
    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, "OK")
        }
        
        // Initialize repositories and services
        val taskRepository = TaskRepository()
        val taskService = TaskService(taskRepository)
        
        // Configure task routes
        taskRoutes(taskService)
    }
}
