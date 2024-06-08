package com.piotr.marketbroker.configuration.startup

import com.piotr.marketbroker.common.logger
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import java.net.UnknownHostException
import java.text.NumberFormat

@Suppress("SpreadOperator", "MagicNumber")
class ApplicationListener(private val environment: Environment) {

    private val log by logger()

    @EventListener(ContextRefreshedEvent::class)
    @Throws(UnknownHostException::class)
    fun contextRefreshedEvent(event: ContextRefreshedEvent) {
        logAppInfo(event)
        logMemoryInfo()
        logEnvInfo()
    }

    private fun logEnvInfo() {
        log.info("=========================== Env Info ============================")
        log.info("JAVA_OPTS" + System.getenv())
        log.info("PROPERTIES" + System.getProperties())
        log.info(LINE_SEPARATOR)
    }

    private fun logMemoryInfo() {
        val format: NumberFormat = NumberFormat.getInstance()

        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory()
        val allocatedMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()

        val mb = (1024 * 1024).toLong()
        val mega = " MB"

        log.info("========================== Memory Info ==========================")
        log.info("Free memory: " + format.format(freeMemory / mb) + mega)
        log.info("Allocated memory: " + format.format(allocatedMemory / mb) + mega)
        log.info("Max memory: " + format.format(maxMemory / mb) + mega)
        log.info("Total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / mb) + mega)
    }

    private fun logAppInfo(event: ContextRefreshedEvent) {
        log.info("=========================== App Info ============================")
        log.info(RUNNING, event.applicationContext.id)
        log.info(PROFILES, listOf(*environment.activeProfiles))
    }

    companion object {
        private const val LINE_SEPARATOR = "================================================================="
        private const val RUNNING = "Application \'{}\' is running! Access URLs:"
        private const val PROFILES = "Profile(s): {}"
    }
}
