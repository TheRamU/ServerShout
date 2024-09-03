package io.github.theramu.servershout.common.util

import java.security.MessageDigest
import java.nio.ByteBuffer
import java.util.*

/**
 * @author TheRamU
 * @since 2024/08/19 05:46
 */
object UuidUtil {

    fun simpleUuid(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    fun toUuid(name: String): UUID {
        val hash = md5("OfflinePlayer:$name")
        val byteArray = ByteArray(16)

        ByteBuffer.wrap(hash).asIntBuffer().apply {
            for (i in byteArray.indices) {
                byteArray[i] = (get(i / 4) shr (24 - (i % 4) * 8) and 0xff).toByte()
            }
        }

        byteArray[6] = (byteArray[6].toInt() and 0x0f or 0x30).toByte()
        byteArray[8] = (byteArray[8].toInt() and 0x3f or 0x80).toByte()

        val uuidStr = buildString {
            byteArray.forEachIndexed { index, byte ->
                append("%02x".format(byte))
                if (index == 3 || index == 5 || index == 7 || index == 9) append('-')
            }
        }
        return UUID.fromString(uuidStr)
    }

    private fun md5(input: String): ByteArray {
        val md = MessageDigest.getInstance("MD5")
        return md.digest(input.toByteArray())
    }
}