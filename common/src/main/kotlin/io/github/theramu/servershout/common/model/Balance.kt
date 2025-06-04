package io.github.theramu.servershout.common.model

import java.sql.ResultSet
import java.util.UUID

/**
 * @author TheRamU
 * @since 2024/08/19 04:32
 */
data class Balance(
    val id: Int,
    val playerUuid: UUID,
    val token: Token,
    var amount: Long
) {

    companion object {
        @JvmStatic
        fun fromResultSet(resultSet: ResultSet) = Balance(
            resultSet.getInt("id"),
            UUID.fromString(resultSet.getString("player_uuid")),
            Token(resultSet.getInt("token_id"), resultSet.getString("token_name")),
            resultSet.getLong("amount")
        )
    }
}