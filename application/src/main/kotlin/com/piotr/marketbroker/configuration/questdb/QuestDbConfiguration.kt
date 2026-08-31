package com.piotr.marketbroker.configuration.questdb

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.sql.Connection

@Configuration
class QuestDbConfiguration {

    @Bean(destroyMethod = "close")
    fun questDbPgDataSource(properties: QuestDbConfigurationProperties): QuestDbPgDataSource {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://${properties.pgHost}:${properties.pgPort}/"
            username = properties.pgUser
            password = properties.pgPassword
            driverClassName = "org.postgresql.Driver"
            poolName = "questdb-pg"
            maximumPoolSize = 5
            minimumIdle = 0
            idleTimeout = 60_000
            connectionTimeout = 5_000
            initializationFailTimeout = -1
            connectionTestQuery = "SELECT 1"
            addDataSourceProperty("sslmode", "disable")
            addDataSourceProperty("preferQueryMode", "simple")
        }
        return QuestDbPgDataSource(HikariDataSource(config))
    }
}

class QuestDbPgDataSource(
    private val hikariDataSource: HikariDataSource
) : AutoCloseable {

    fun getConnection(): Connection = hikariDataSource.connection

    override fun close() = hikariDataSource.close()
}
