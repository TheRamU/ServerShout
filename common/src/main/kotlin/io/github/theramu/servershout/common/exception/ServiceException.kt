package io.github.theramu.servershout.common.exception

/**
 * @author TheRamU
 * @since 2024/8/27 4:30
 */
class ServiceException(
    val code: Int,
    override val message: String? = null,
    val language: String? = null,
    val args: Array<Any> = emptyArray()
) : RuntimeException()