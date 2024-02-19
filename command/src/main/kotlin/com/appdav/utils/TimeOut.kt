package com.appdav.utils

import java.util.concurrent.TimeUnit

/**
 * Model that describes timeout
 */
data class TimeOut(
    /**
     * Timeout value
     */
    val time: Long,
    /**
     * Timeout value units
     */
    val timeUnit: TimeUnit
)
