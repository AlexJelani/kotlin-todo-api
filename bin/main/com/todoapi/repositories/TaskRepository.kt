package com.todoapi.repositories

import com.todoapi.models.Task
import com.todoapi.models.Tasks
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskRepository {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun getAllTasks(): List<Task> = transaction {
        Tasks.selectAll().map { resultRow ->
            mapToTask(resultRow)
        }
    }

    fun getTaskById(id: Int): Task? = transaction {
        Tasks.select { Tasks.id eq id }
            .map { mapToTask(it) }
            .singleOrNull()
    }

    fun createTask(task: Task): Task = transaction {
        val insertStatement = Tasks.insert {
            it[title] = task.title
            it[description] = task.description
            it[completed] = task.completed
        }
        
        val resultRow = insertStatement.resultedValues?.first()
        if (resultRow != null) {
            mapToTask(resultRow)
        } else {
            throw Exception("Failed to create task")
        }
    }

    fun updateTask(id: Int, task: Task): Boolean = transaction {
        val now = LocalDateTime.now()
        Tasks.update({ Tasks.id eq id }) {
            it[title] = task.title
            it[description] = task.description
            it[completed] = task.completed
            it[updatedAt] = now
        } > 0
    }

    fun deleteTask(id: Int): Boolean = transaction {
        Tasks.deleteWhere { Tasks.id eq id } > 0
    }

    private fun mapToTask(row: ResultRow): Task = Task(
        id = row[Tasks.id],
        title = row[Tasks.title],
        description = row[Tasks.description],
        completed = row[Tasks.completed],
        createdAt = row[Tasks.createdAt].format(formatter),
        updatedAt = row[Tasks.updatedAt].format(formatter)
    )
}
