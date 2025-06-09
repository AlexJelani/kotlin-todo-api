package com.todoapi.services

import com.todoapi.models.Task
import com.todoapi.repositories.TaskRepository

class TaskService(private val repository: TaskRepository) {
    fun getAllTasks(): List<Task> = repository.getAllTasks()
    
    fun getTaskById(id: Int): Task? = repository.getTaskById(id)
    
    fun createTask(task: Task): Task = repository.createTask(task)
    
    fun updateTask(id: Int, task: Task): Boolean = repository.updateTask(id, task)
    
    fun deleteTask(id: Int): Boolean = repository.deleteTask(id)
}
