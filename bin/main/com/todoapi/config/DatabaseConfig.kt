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
        val env = System.getenv()
        val appDbConfig = config.config("database") // Ktor's ApplicationConfig for "database" node

        // Determine JDBC URL: Prioritize JDBC_DATABASE_URL, then construct from DB_HOST/PORT/NAME, then application.conf, then defaults.
        val jdbcUrl = env["JDBC_DATABASE_URL"] ?: run {
            val host = env["DB_HOST"] ?: appDbConfig.propertyOrNull("host")?.getString() ?: "localhost"
            val port = env["DB_PORT"] ?: appDbConfig.propertyOrNull("port")?.getString() ?: "5432"
            val name = env["DB_NAME"] ?: appDbConfig.propertyOrNull("name")?.getString() ?: "postgres" // Default DB name
            "jdbc:postgresql://$host:$port/$name"
        }
        
        // Determine User: Prioritize JDBC_DATABASE_USERNAME, then DB_USER, then application.conf, then default "postgres".
        val user = env["JDBC_DATABASE_USERNAME"]
            ?: env["DB_USER"]
            ?: appDbConfig.propertyOrNull("user")?.getString()
            ?: "postgres" // Fallback user

        // Determine Password: Prioritize JDBC_DATABASE_PASSWORD, then DB_PASSWORD, then application.conf. Fallback to empty string.
        val password = env["JDBC_DATABASE_PASSWORD"]
            ?: env["DB_PASSWORD"]
            ?: appDbConfig.propertyOrNull("password")?.getString()
            ?: "" // Fallback password

        println("Attempting to connect to database.")
        println("JDBC URL: $jdbcUrl")
        println("User: $user")
        // Avoid printing password directly: println("Password: ${if (password.isNotEmpty()) "********" else "not set"}")
        
        val hikariConfig = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = user
            this.password = password
            this.driverClassName = "org.postgresql.Driver"
            this.maximumPoolSize = 10 // Consider making this configurable
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            // Recommended HikariCP settings
            this.connectionTimeout = 30000 // 30 seconds
            this.idleTimeout = 600000 // 10 minutes
            this.maxLifetime = 1800000 // 30 minutes
            this.validate()
        }

        try {
            val dataSource = HikariDataSource(hikariConfig)
            Database.connect(dataSource)
            println("Database connection established successfully.")

            transaction {
                SchemaUtils.create(Tasks)
                println("Schema initialized (Tasks table created if not exists).")
            }
        } catch (e: Exception) {
            println("ERROR: Failed to connect to the database or initialize schema.")
            println("JDBC URL used: $jdbcUrl")
            println("User used: $user")
            println("Ensure DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD (or their JDBC_ equivalents) are correctly set in the environment.")
            e.printStackTrace() // Log the full stack trace for detailed error
            throw e // Re-throw to prevent application from starting in a broken state
        }
    }
}
