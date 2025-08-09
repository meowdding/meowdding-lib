package me.owdding.lib.utils

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface MeowddingLogger {

    companion object {
        internal val STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
        fun named(name: String): MeowddingLogger = MeowddingLoggerImpl(LoggerFactory.getLogger(name))
        fun autoResolve(): MeowddingLogger = MeowddingLoggerImpl(LoggerFactory.getLogger(STACK_WALKER.callerClass))

        fun MeowddingLogger.featureLogger(): MeowddingLogger {
            val name = STACK_WALKER.callerClass.let { it.annotations.filterIsInstance<FeatureName>().firstOrNull()?.name ?: it.simpleName }

            return featureLogger(name)
        }
    }


    fun featureLogger(name: String): MeowddingLogger

    fun trace(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun debug(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun info(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun warn(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun error(message: String, throwable: Throwable? = null, prefix: String? = null)

}

annotation class FeatureName(val name: String)

internal data class MeowddingLoggerImpl(val logger: Logger, val defaultPrefix: String? = null) : MeowddingLogger {
    fun mergedPrefix(prefix: String?) = listOfNotNull(defaultPrefix, prefix).joinToString("/")

    private fun wrapMessage(message: String, prefix: String?): String {
        val prefix = mergedPrefix(prefix).takeUnless { it.isBlank() }
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            return "${prefix?.let { "<$it> " } ?: ""}$message"
        } else {
            return "[${logger.name}${prefix?.let { "/$it" } ?: ""}] $message"
        }
    }

    private fun log(message: String, throwable: Throwable?, prefix: String?, noError: (String) -> Unit, error: (String, Throwable) -> Unit) {
        val message = wrapMessage(message, prefix)
        if (throwable != null) {
            error(message, throwable)
        } else {
            noError(message)
        }
    }

    override fun featureLogger(name: String): MeowddingLogger {
        return MeowddingLoggerImpl(logger, mergedPrefix(name))
    }

    override fun trace(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, logger::trace, logger::trace)

    override fun debug(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, logger::debug, logger::debug)

    override fun info(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, logger::info, logger::info)

    override fun warn(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, logger::warn, logger::warn)

    override fun error(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, logger::error, logger::error)

}
