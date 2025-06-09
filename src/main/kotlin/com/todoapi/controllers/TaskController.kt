package com.todoapi.controllers

import com.todoapi.models.Task
import com.todoapi.services.TaskService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoutes(taskService: TaskService) {
    // Health check endpoint for Cloud Run
    get("/") {
        call.respondText("Kotlin Todo API is running!", ContentType.Text.Plain)
    }
    
    // API routes
    route("/api/tasks") {
        get {
            val tasks = taskService.getAllTasks()
            call.respond(tasks)
        }
        
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@get
            }
            
            val task = taskService.getTaskById(id)
            if (task != null) {
                call.respond(task)
            } else {
                call.respond(HttpStatusCode.NotFound, "Task not found")
            }
        }
        
        post {
            val task = call.receive<Task>()
            val createdTask = taskService.createTask(task)
            call.respond(HttpStatusCode.Created, createdTask)
        }
        
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@put
            }
            
            val task = call.receive<Task>()
            val updatedTask = taskService.updateTask(id, task)
            if (updatedTask != null) {
                call.respond(updatedTask)
            } else {
                call.respond(HttpStatusCode.NotFound, "Task not found")
            }
        }
        
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@delete
            }
            
            val deleted = taskService.deleteTask(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Task not found")
            }
        }
    }
}
