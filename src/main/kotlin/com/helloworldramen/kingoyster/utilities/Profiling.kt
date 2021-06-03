package com.helloworldramen.kingoyster.utilities

import kotlin.system.measureNanoTime

object Profiling {

    fun measureMs(block: () -> Unit): String {
        return formatNano(measureNanoTime(block))
    }

    fun formatNano(nano: Long): String {
        return String.format("%.2f", nano / 1000000.0)
    }
}