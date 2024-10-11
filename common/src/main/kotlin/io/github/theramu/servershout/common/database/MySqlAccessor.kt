package io.github.theramu.servershout.common.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.balance.Balance
import io.github.theramu.servershout.common.token.Token
import io.github.theramu.servershout.common.util.UuidUtil
import java.io.Closeable
import java.sql.SQLException
import java.util.*

/**
 * @author TheRamU
 * @since 2024/08/19 02:48
 */
class MySqlAccessor : Closeable {

    private val api get() = ServerShoutApi.api
    private val logger get() = api.logger
    private val settings get() = api.configLoader.databaseSettings
    private lateinit var dataSource: HikariDataSource

    private val tokensTableName: String get() = "${settings.tablePrefix}tokens"
    private val balancesTableName: String get() = "${settings.tablePrefix}balances"
    private val balancesViewName: String get() = "${settings.tablePrefix}balances_view"
    private val legacyTableName: String get() = "servershout"

    fun connect() {
        try {
            if (this::dataSource.isInitialized) {
                dataSource.close()
            }
        } catch (_: Exception) {
        }
        if (!settings.enabled) {
            return
        }

        logger.info("Connecting to MySQL database...")
        val config = HikariConfig()
        try {
            config.driverClassName = "com.mysql.cj.jdbc.Driver"
        } catch (e: Exception) {
            logger.warn("Failed to load MySQL driver, try to load default driver.")
        }
        config.jdbcUrl = "jdbc:mysql://${settings.host}:${settings.port}/${settings.database}?${settings.options}"
        config.username = settings.username
        config.password = settings.password

        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        config.poolName = "ServerShout"
        config.maximumPoolSize = settings.poolSettings.maxSize
        config.minimumIdle = settings.poolSettings.minIdle // 最小空闲连接数
        config.idleTimeout = settings.poolSettings.idleTimeout // 空闲连接超时时间
        config.connectionTimeout = settings.poolSettings.connectionTimeout // 连接超时时间
        config.maxLifetime = settings.poolSettings.maxLifetime // 连接最大存活时间
        config.validate()

        dataSource = HikariDataSource(config)

        this.createTables()
    }

    override fun close() {
        if (this::dataSource.isInitialized) {
            dataSource.close()
        }
    }

    fun isClosed(): Boolean {
        return !this::dataSource.isInitialized || dataSource.isClosed
    }

    fun queryBalance(uuid: UUID, token: Token): Balance? {
        return dataSource.connection.use { connection ->
            val sql = "SELECT * FROM `${balancesViewName}` WHERE `player_uuid` = ? AND `token_id` = ?"
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, uuid.toString())
                statement.setInt(2, token.id)
                statement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        Balance.fromResultSet(resultSet)
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun updateBalance(uuid: UUID, token: Token, amount: Long) {
        dataSource.connection.use { connection ->
            val upsertSql = """
            INSERT INTO `${balancesTableName}` (`player_uuid`, `token_id`, `amount`)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE `amount` = VALUES(`amount`)
        """.trimIndent()
            connection.prepareStatement(upsertSql).use { statement ->
                statement.setString(1, uuid.toString())
                statement.setInt(2, token.id)
                statement.setLong(3, amount)
                statement.executeUpdate()
            }
        }
    }

    fun takeBalance(uuid: UUID, token: Token, amount: Long): Boolean {
        return dataSource.connection.use { connection ->
            val updateSql = "UPDATE `${balancesTableName}` SET `amount` = `amount` - ? WHERE `player_uuid` = ? AND `token_id` = ? AND `amount` >= ?"
            connection.prepareStatement(updateSql).use { statement ->
                statement.setLong(1, amount)
                statement.setString(2, uuid.toString())
                statement.setInt(3, token.id)
                statement.setLong(4, amount)
                statement.executeUpdate() > 0
            }
        }
    }

    fun giveBalance(uuid: UUID, token: Token, amount: Long): Boolean {
        return dataSource.connection.use { connection ->
            val upsertSql = """
            INSERT INTO `${balancesTableName}` (`player_uuid`, `token_id`, `amount`)
            VALUES (?, (SELECT `id` FROM `${tokensTableName}` WHERE `name` = ?), ?)
            ON DUPLICATE KEY UPDATE `amount` = `amount` + VALUES(`amount`)
        """.trimIndent()
            connection.prepareStatement(upsertSql).use { statement ->
                statement.setString(1, uuid.toString())
                statement.setString(2, token.name)
                statement.setLong(3, amount)
                statement.executeUpdate() > 0
            }
        }
    }

    fun insertToken(name: String): Int {
        dataSource.connection.use { connection ->
            val checkSql = "SELECT `id`, `deleted` FROM `${tokensTableName}` WHERE `name` = ?"
            connection.prepareStatement(checkSql).use { checkStatement ->
                checkStatement.setString(1, name)
                checkStatement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        val id = resultSet.getInt("id")
                        val deleted = resultSet.getInt("deleted")
                        if (deleted == 1) {
                            // 恢复已删除的 token 类型
                            val updateSql = "UPDATE `${tokensTableName}` SET `deleted` = 0 WHERE `id` = ?"
                            connection.prepareStatement(updateSql).use { updateStatement ->
                                updateStatement.setInt(1, id)
                                updateStatement.executeUpdate()
                            }
                        }
                        return id
                    } else {
                        // 插入新的 token 类型
                        val insertSql = "INSERT INTO `${tokensTableName}` (`name`) VALUES (?)"
                        connection.prepareStatement(insertSql, java.sql.Statement.RETURN_GENERATED_KEYS).use { insertStatement ->
                            insertStatement.setString(1, name)
                            insertStatement.executeUpdate()
                            insertStatement.generatedKeys.use { keys ->
                                if (keys.next()) {
                                    return keys.getInt(1)
                                }
                            }
                        }
                    }
                }
            }
            throw RuntimeException("Failed to insert token type")
        }
    }

    fun deleteToken(id: Int) {
        dataSource.connection.use { connection ->
            val sql = "UPDATE `${tokensTableName}` SET `deleted` = 1 WHERE `id` = ?"
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeUpdate()
            }
        }
    }

    fun queryTokenList(): List<Token> {
        return dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT * FROM `${tokensTableName}` WHERE `deleted` = 0").use { resultSet ->
                    val tokens = mutableListOf<Token>()
                    while (resultSet.next()) {
                        tokens.add(Token.fromResultSet(resultSet))
                    }
                    tokens
                }
            }
        }
    }

    private fun createTables() {
        dataSource.connection.use { connection ->
            connection.autoCommit = false
            try {
                createTablesIfNotExists(connection)
                insertDefaultTokensIfNeeded(connection)
                connection.commit()
            } catch (e: SQLException) {
                connection.rollback()
                logger.error("Failed to create tables", e)
                throw e
            } finally {
                connection.autoCommit = true
            }
        }
    }

    private fun createTablesIfNotExists(connection: java.sql.Connection) {
        connection.createStatement().use { statement ->
            statement.addBatch(
                """
                CREATE TABLE IF NOT EXISTS `${tokensTableName}` (
                    `id`   INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    `name` VARCHAR(16) NOT NULL UNIQUE,
                    `deleted` INT DEFAULT 0 NOT NULL
                ) CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
                """.trimIndent()
            )
            statement.addBatch(
                """
                CREATE TABLE IF NOT EXISTS `${balancesTableName}` (
                    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    `player_uuid` CHAR(36) NOT NULL,
                    `token_id` INT NOT NULL,
                    `amount` BIGINT NOT NULL,
                    FOREIGN KEY (`token_id`) REFERENCES `${tokensTableName}` (`id`) ON DELETE CASCADE,
                    UNIQUE KEY `unique_uuid_token` (`player_uuid`, `token_id`),
                    INDEX `idx_uuid_token` (`player_uuid`, `token_id`)
                ) CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """.trimIndent()
            )
            statement.addBatch(
                """
                CREATE OR REPLACE VIEW `${balancesViewName}` AS
                SELECT pt.id, pt.player_uuid, pt.token_id, pt.amount, t.name AS token_name
                FROM `${balancesTableName}` pt
                JOIN `${tokensTableName}` t ON pt.token_id = t.id
                WHERE t.deleted = 0;
                """.trimIndent()
            )
            statement.executeBatch()
        }
    }

    private fun insertDefaultTokensIfNeeded(connection: java.sql.Connection) {
        val querySql = "SELECT COUNT(*) FROM `${tokensTableName}`"
        connection.createStatement().use { statement ->
            statement.executeQuery(querySql).use { resultSet ->
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    insertToken("SHOUT")
                    insertToken("INVITE")
                }
            }
        }
    }

    fun migrate(token: Token): Int {
        dataSource.connection.use { connection ->
            val metaData = connection.metaData
            val resultSet = metaData.getTables(null, null, legacyTableName, null)
            if (!resultSet.next()) {
                return -1
            }

            val sql = "SELECT `name`, `shout` FROM `${legacyTableName}`"

            // 插入到新的表，如果已存在则不插入
            val insertSql = """
                INSERT INTO `${balancesTableName}` (`player_uuid`, `token_id`, `amount`)
                SELECT ?, ?, ?
                WHERE NOT EXISTS (
                    SELECT 1 FROM `${balancesTableName}`
                    WHERE `player_uuid` = ? AND `token_id` = ?
                )
            """.trimIndent()
            var affectedCount = 0
            connection.prepareStatement(insertSql).use { insertStatement ->
                connection.prepareStatement(sql).use { statement ->
                    statement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            val name = resultSet.getString("name")
                            val shout = resultSet.getInt("shout")
                            val uuid = UuidUtil.toUuid(name).toString()
                            insertStatement.setString(1, uuid)
                            insertStatement.setInt(2, token.id)
                            insertStatement.setLong(3, shout.toLong())
                            insertStatement.setString(4, uuid)
                            insertStatement.setInt(5, token.id)

                            affectedCount += insertStatement.executeUpdate()
                        }
                    }
                }
            }
            return affectedCount
        }
    }
}