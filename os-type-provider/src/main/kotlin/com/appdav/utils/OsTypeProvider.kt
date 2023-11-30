package com.appdav.utils

import com.appdav.utils.OsType.*

/**
 * Provides type of current operating system
 */
object OsTypeProvider {

    /**
     * Current OS type
     */
    val current: OsType by lazy { determineOsType() }

    private fun determineOsType(): OsType {
        val os = System.getProperty("os.name", "generic").lowercase()
        return when {
            os.contains("mac") ||
                    os.contains("darwin") -> MAC

            os.contains("win") -> WINDOWS
            os.contains("nux") ||
                    os.contains("nix") -> LINUX

            else -> OTHER
        }
    }

}
