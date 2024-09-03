package io.github.theramu.servershout.common.token

import java.sql.ResultSet

/**
 * @author TheRamU
 * @since 2024/08/19 4:31
 */
data class Token(
    val id: Int,
    val name: String
) {
    companion object {
        @JvmStatic
        fun fromResultSet(resultSet: ResultSet) = Token(
            resultSet.getInt("id"),
            resultSet.getString("name")
        )
    }
}
