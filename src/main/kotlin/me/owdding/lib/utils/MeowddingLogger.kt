package me.owdding.lib.utils

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface MeowddingLogger {
    companion object {
        internal val logAll: Boolean = java.lang.Boolean.getBoolean("meowdding.debug.log")

        internal val STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
        fun named(name: String): MeowddingLogger = MeowddingLoggerImpl(LoggerFactory.getLogger(name))
        fun autoResolve(): MeowddingLogger = MeowddingLoggerImpl(LoggerFactory.getLogger(STACK_WALKER.callerClass.simpleName))

        fun MeowddingLogger.featureLogger(): MeowddingLogger {
            val name = STACK_WALKER.callerClass.let { it.annotations.filterIsInstance<FeatureName>().firstOrNull()?.name ?: it.simpleName }

            return featureLogger(name)
        }
    }


    fun featureLogger(name: String): MeowddingLogger

    fun trace(message: String) {
        trace(message, null, null)
    }

    fun trace(message: String, throwable: Throwable) {
        trace(message, throwable, null)
    }

    fun trace(message: String, prefix: String) {
        trace(message, null, prefix)
    }

    fun trace(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun debug(message: String) {
        debug(message, null, null)
    }

    fun debug(message: String, throwable: Throwable) {
        debug(message, throwable, null)
    }

    fun debug(message: String, prefix: String) {
        debug(message, null, prefix)
    }

    fun debug(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun info(message: String) {
        info(message, null, null)
    }

    fun info(message: String, throwable: Throwable) {
        info(message, throwable, null)
    }

    fun info(message: String, prefix: String) {
        info(message, null, prefix)
    }

    fun info(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun warn(message: String) {
        warn(message, null, null)
    }

    fun warn(message: String, throwable: Throwable) {
        warn(message, throwable, null)
    }

    fun warn(message: String, prefix: String) {
        warn(message, null, prefix)
    }

    fun warn(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun error(message: String) {
        error(message, null, null)
    }

    fun error(message: String, throwable: Throwable) {
        error(message, throwable, null)
    }

    fun error(message: String, prefix: String) {
        error(message, null, prefix)
    }

    fun error(message: String, throwable: Throwable? = null, prefix: String? = null)

}

annotation class FeatureName(val name: String)

internal data class MeowddingLoggerImpl(val logger: Logger, val defaultPrefix: String? = null) : MeowddingLogger {
    enum class LogLevel(val noError: Logger.(String) -> Unit, val error: Logger.(String, Throwable) -> Unit) {
        TRACE(Logger::trace, Logger::trace),
        DEBUG(Logger::debug, Logger::debug),
        INFO(Logger::info, Logger::info),
        WARN(Logger::warn, Logger::warn),
        ERROR(Logger::error, Logger::error),
    }

    fun mergedPrefix(prefix: String?) = listOfNotNull(defaultPrefix, prefix).joinToString("/")

    private fun wrapMessage(message: String, prefix: String?): String {
        val prefix = mergedPrefix(prefix).takeUnless { it.isBlank() }
        return if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            "${prefix?.let { "<$it> " } ?: ""}$message"
        } else {
            "[${logger.name}${prefix?.let { "/$it" } ?: ""}] $message"
        }
    }

    private fun log(message: String, throwable: Throwable?, prefix: String?, logLevel: LogLevel) {
        val level = if (MeowddingLogger.logAll) {
            listOf(logLevel, LogLevel.INFO).max()
        } else {
            logLevel
        }

        val message = if (level != logLevel) {
            "[${logLevel.name}] ${wrapMessage(message, prefix)}"
        } else wrapMessage(message, prefix)

        if (throwable != null) {
            level.error(logger, message, throwable)
        } else {
            level.noError(logger, message)
        }
    }

    override fun featureLogger(name: String): MeowddingLogger {
        return MeowddingLoggerImpl(logger, mergedPrefix(name))
    }

    override fun trace(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, LogLevel.TRACE)

    override fun debug(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, LogLevel.DEBUG)

    override fun info(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, LogLevel.INFO)

    override fun warn(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, LogLevel.WARN)

    override fun error(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, LogLevel.ERROR)

}
