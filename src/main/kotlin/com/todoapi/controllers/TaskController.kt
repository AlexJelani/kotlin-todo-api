package com.todoapi.controllers

import com.todoapi.models.Task
import com.todoapi.services.TaskService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoutes(taskService: TaskService) {
    route("/api/tasks") {
        // Get all tasks
        get {
            val tasks = taskService.getAllTasks()
            call.respond(tasks)
        }
        
        // Get task by ID
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
        
        // Create new task
        post {
            val task = call.receive<Task>()
            val createdTask = taskService.createTask(task)
            call.respond(HttpStatusCode.Created, createdTask)
        }
        
        // Update task
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@put
            }
            
            val task = call.receive<Task>()
            val updated = taskService.updateTask(id, task)
            
            if (updated) {
                call.respond(HttpStatusCode.OK, "Task updated successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Task not found")
            }
        }
        
        // Delete task
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@delete
            }
            
            val deleted = taskService.deleteTask(id)
            
            if (deleted) {
                call.respond(HttpStatusCode.OK, "Task deleted successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Task not found")
            }
        }
    }
}
