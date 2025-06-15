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
        // Get database connection info from config
        val databaseConfig = config.config("database")
        val host = databaseConfig.propertyOrNull("host")?.getString()
        val port = databaseConfig.propertyOrNull("port")?.getString()
        val name = databaseConfig.propertyOrNull("name")?.getString()
        val jdbcUrl = "jdbc:postgresql://$host:$port/$name"
        
        val user = databaseConfig.propertyOrNull("user")?.getString()
        val password = databaseConfig.propertyOrNull("password")?.getString()

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
