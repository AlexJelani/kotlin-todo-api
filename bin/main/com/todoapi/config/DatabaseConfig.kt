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
            val host = databaseConfig.property("host").getString()
            val port = databaseConfig.property("port").getString()
            val name = databaseConfig.property("name").getString()
            "jdbc:postgresql://$host:$port/$name"
        }
        
        val user = System.getenv("JDBC_DATABASE_USERNAME") 
            ?: config.config("database").property("user").getString()
        val password = System.getenv("JDBC_DATABASE_PASSWORD") 
            ?: config.config("database").property("password").getString()

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
