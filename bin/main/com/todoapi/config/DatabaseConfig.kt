package com.todoapi.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.todoapi.models.Tasks

object DatabaseConfig {
    fun init(config: ApplicationConfig) {
        // Try to get database connection info from environment variables first
        // Fall back to application.conf if environment variables are not set
        val jdbcUrl = System.getenv("JDBC_DATABASE_URL") ?: run {
            val databaseConfig = config.config("database")
            val host = databaseConfig.propertyOrNull("host")?.getString() ?: "localhost"
            val port = databaseConfig.propertyOrNull("port")?.getString() ?: "5432"
            val name = databaseConfig.propertyOrNull("name")?.getString() ?: "postgres"
            "jdbc:postgresql://$host:$port/$name"
        }
        
        val user = System.getenv("JDBC_DATABASE_USERNAME") 
            ?: config.config("database").propertyOrNull("user")?.getString() ?: "postgres"
        val password = System.getenv("JDBC_DATABASE_PASSWORD") 
            ?: config.config("database").propertyOrNull("password")?.getString() ?: ""

        println("Connecting to database: $jdbcUrl with user: $user")
        
        val hikariConfig = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = user
            this.password = password
            this.driverClassName = "org.postgresql.Driver"
            this.maximumPoolSize = 10
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            this.validate()
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)

        // Create tables if they don't exist
        transaction {
            SchemaUtils.create(Tasks)
        }
    }
}
