package me.owdding.lib.utils

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public interface MeowddingLogger {
    public companion object {
        internal val logAll: Boolean = java.lang.Boolean.getBoolean("meowdding.debug.log")

        internal val STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
        public fun named(name: String): MeowddingLogger = MeowddingLoggerImpl(LoggerFactory.getLogger(name))
        public fun autoResolve(): MeowddingLogger = MeowddingLoggerImpl(LoggerFactory.getLogger(STACK_WALKER.callerClass.simpleName))

        public fun MeowddingLogger.featureLogger(): MeowddingLogger {
            val name = STACK_WALKER.callerClass.let { it.annotations.filterIsInstance<FeatureName>().firstOrNull()?.name ?: it.simpleName }

            return featureLogger(name)
        }
    }


    public fun featureLogger(name: String): MeowddingLogger

    public fun trace(message: String) {
        trace(message, null, null)
    }

    public fun trace(message: String, throwable: Throwable) {
        trace(message, throwable, null)
    }

    public fun trace(message: String, prefix: String) {
        trace(message, null, prefix)
    }

    public fun trace(message: String, throwable: Throwable? = null, prefix: String? = null)

    public fun debug(message: String) {
        debug(message, null, null)
    }

    public fun debug(message: String, throwable: Throwable) {
        debug(message, throwable, null)
    }

    public fun debug(message: String, prefix: String) {
        debug(message, null, prefix)
    }

    public fun debug(message: String, throwable: Throwable? = null, prefix: String? = null)

    public fun info(message: String) {
        info(message, null, null)
    }

    public fun info(message: String, throwable: Throwable) {
        info(message, throwable, null)
    }

    public fun info(message: String, prefix: String) {
        info(message, null, prefix)
    }

    public fun info(message: String, throwable: Throwable? = null, prefix: String? = null)

    public fun warn(message: String) {
        warn(message, null, null)
    }

    public fun warn(message: String, throwable: Throwable) {
        warn(message, throwable, null)
    }

    public fun warn(message: String, prefix: String) {
        warn(message, null, prefix)
    }

    public fun warn(message: String, throwable: Throwable? = null, prefix: String? = null)

    public fun error(message: String) {
        error(message, null, null)
    }

    public fun error(message: String, throwable: Throwable) {
        error(message, throwable, null)
    }

    public fun error(message: String, prefix: String) {
        error(message, null, prefix)
    }

    public fun error(message: String, throwable: Throwable? = null, prefix: String? = null)

    public fun <Return> runCatching(message: String, block: () -> Return) : Return? = runCatching {
        block()
    }.getOrElse {
        error("An error occurred during task: $message", it)
        null
    }

}

public annotation class FeatureName(val name: String)

internal data class MeowddingLoggerImpl(val logger: Logger, val defaultPrefix: String? = null) : MeowddingLogger {
    enum class LogLevel(val noError: Logger.(String) -> Unit, val error: Logger.(String, Throwable) -> Unit) {
        TRACE(Logger::trace, Logger::trace),
        DEBUG(Logger::debug, Logger::debug),
        INFO(Logger::info, Logger::info),
        WARN(Logger::warn, Logger::warn),
        ERROR(Logger::error, Logger::error),
    }

    public fun mergedPrefix(prefix: String?) = listOfNotNull(defaultPrefix, prefix).joinToString("/")

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
