package io.github.theramu.servershout.common.platform.logging

import org.slf4j.Logger
import org.slf4j.Marker

/**
 * @author TheRamU
 * @since 2024/8/20 23:41
 */
abstract class PlatformLogger : Logger {
    abstract override fun info(message: String?, vararg args: Any?)
    abstract override fun error(message: String?, vararg args: Any?)
    abstract override fun warn(message: String?, vararg args: Any?)

    abstract fun sendMessage(receiver: Any, message: String, vararg args: Any)
    abstract fun sendLanguageMessage(receiver: Any, path: String, vararg args: Any)

    override fun getName(): String {
        return "ServerShout"
    }

    override fun isTraceEnabled(): Boolean {
        return false
    }

    override fun isTraceEnabled(p0: Marker?): Boolean {
        return false
    }

    override fun trace(p0: String?) {
        throw NotImplementedError()
    }

    override fun trace(p0: String, p1: Any?) {
        throw NotImplementedError()
    }

    override fun trace(p0: String, p1: Any?, p2: Any?) {
        throw NotImplementedError()
    }

    override fun trace(p0: String?, vararg p1: Any?) {
        throw NotImplementedError()
    }

    override fun trace(p0: String?, p1: Throwable?) {
        throw NotImplementedError()
    }

    override fun trace(p0: Marker?, p1: String?) {
        throw NotImplementedError()
    }

    override fun trace(p0: Marker?, p1: String?, p2: Any?) {
        throw NotImplementedError()
    }

    override fun trace(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {
        throw NotImplementedError()
    }

    override fun trace(p0: Marker?, p1: String?, vararg p2: Any?) {
        throw NotImplementedError()
    }

    override fun trace(p0: Marker?, p1: String?, p2: Throwable?) {
        throw NotImplementedError()
    }

    override fun isDebugEnabled(): Boolean {
        throw NotImplementedError()
    }

    override fun isDebugEnabled(p0: Marker?): Boolean {
        throw NotImplementedError()
    }

    override fun debug(p0: String?) {
        throw NotImplementedError()
    }

    override fun debug(p0: String?, p1: Any?) {
        throw NotImplementedError()
    }

    override fun debug(p0: String?, p1: Any?, p2: Any?) {
        throw NotImplementedError()
    }

    override fun debug(p0: String?, vararg p1: Any?) {
        throw NotImplementedError()
    }

    override fun debug(p0: String?, p1: Throwable?) {
        throw NotImplementedError()
    }

    override fun debug(p0: Marker?, p1: String?) {
        throw NotImplementedError()
    }

    override fun debug(p0: Marker?, p1: String?, p2: Any?) {
        throw NotImplementedError()
    }

    override fun debug(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {
        throw NotImplementedError()
    }

    override fun debug(p0: Marker?, p1: String?, vararg p2: Any?) {
        throw NotImplementedError()
    }

    override fun debug(p0: Marker?, p1: String?, p2: Throwable?) {
        throw NotImplementedError()
    }

    override fun isInfoEnabled(): Boolean {
        throw NotImplementedError()
    }

    override fun isInfoEnabled(p0: Marker?): Boolean {
        throw NotImplementedError()
    }

    override fun info(p0: String?) {
        this.info(p0, *emptyArray<Any>())
    }

    override fun info(p0: String?, p1: Any?) {
        this.info(p0, p1, *emptyArray<Any>())
    }

    override fun info(p0: String?, p1: Any?, p2: Any?) {
        this.info(p0, p1, p2, *emptyArray<Any>())
    }

    override fun info(p0: String?, p1: Throwable?) {
        this.info(p0 + '\n' + p1?.stackTraceToString())
    }

    override fun info(p0: Marker?, p1: String?) {
        throw NotImplementedError()
    }

    override fun info(p0: Marker?, p1: String?, p2: Any?) {
        throw NotImplementedError()
    }

    override fun info(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {
        throw NotImplementedError()
    }

    override fun info(p0: Marker?, p1: String?, vararg p2: Any?) {
        throw NotImplementedError()
    }

    override fun info(p0: Marker?, p1: String?, p2: Throwable?) {
        throw NotImplementedError()
    }

    override fun isWarnEnabled(): Boolean {
        return true
    }

    override fun isWarnEnabled(p0: Marker?): Boolean {
        return false
    }

    override fun warn(p0: String?) {
        this.warn(p0, *emptyArray<Any>())
    }

    override fun warn(p0: String?, p1: Any?) {
        this.warn(p0, p1, *emptyArray<Any>())
    }

    override fun warn(p0: String?, p1: Any?, p2: Any?) {
        this.warn(p0, p1, p2, *emptyArray<Any>())
    }

    override fun warn(p0: String?, p1: Throwable?) {
        this.warn(p0 + '\n' + p1?.stackTraceToString())
    }

    override fun warn(p0: Marker?, p1: String?) {
        throw NotImplementedError()
    }

    override fun warn(p0: Marker?, p1: String?, p2: Any?) {
        throw NotImplementedError()
    }

    override fun warn(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {
        throw NotImplementedError()
    }

    override fun warn(p0: Marker?, p1: String?, vararg p2: Any?) {
        throw NotImplementedError()
    }

    override fun warn(p0: Marker?, p1: String?, p2: Throwable?) {
        throw NotImplementedError()
    }

    override fun isErrorEnabled(): Boolean {
        return true
    }

    override fun isErrorEnabled(p0: Marker?): Boolean {
        return false
    }

    override fun error(p0: String?) {
        this.error(p0, *emptyArray<Any>())
    }

    override fun error(p0: String?, p1: Any?) {
        this.error(p0, p1, *emptyArray<Any>())
    }

    override fun error(p0: String?, p1: Any?, p2: Any?) {
        this.error(p0, p1, p2, *emptyArray<Any>())
    }

    override fun error(p0: String?, p1: Throwable?) {
        this.error(p0 + '\n' + p1?.stackTraceToString())
    }

    override fun error(p0: Marker?, p1: String?) {
        throw NotImplementedError()
    }

    override fun error(p0: Marker?, p1: String?, p2: Any?) {
        throw NotImplementedError()
    }

    override fun error(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {
        throw NotImplementedError()
    }

    override fun error(p0: Marker?, p1: String?, vararg p2: Any?) {
        throw NotImplementedError()
    }

    override fun error(p0: Marker?, p1: String?, p2: Throwable?) {
        throw NotImplementedError()
    }
}
