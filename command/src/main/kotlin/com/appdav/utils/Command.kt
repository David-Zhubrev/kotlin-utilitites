package com.appdav.utils

import java.io.File
import kotlin.system.exitProcess

private fun currentWorkingDir() = File("").absoluteFile

/**
 * Command class provides fast way to use command-line commands
 */
class Command private constructor(
    private val command: List<String>,
    private val id: Int = nextId(),
    private val outputRedirect: File = File(
        File(System.getenv("user.home"), "tmp"),
        "tmp${File.separatorChar}output_$id.txt"
    ).apply {
        if (!parentFile.exists()) parentFile.mkdirs()
        createNewFile()
        deleteOnExit()
    },
) {
    /**
     * @param command String command to launch
     */
    constructor(command: String) : this(command.split(" "))

    /**
     * @param command String command to launch
     * @param outputRedirect file where command output will be redirected
     */
    constructor(command: String, outputRedirect: File) : this(command.split(" "), outputRedirect)

    /**
     * @param command list of command-line arguments to launch. For instance, if you want to launch "java --version", provide listOf("java", "--version")
     */
    constructor(command: List<String>) : this(command, id = nextId())

    /**
     * @param command list of command-line arguments to launch. For instance, if you want to launch "java --version", provide listOf("java", "--version")
     * @param outputRedirect file where command output will be redirected
     */
    constructor(command: List<String>, outputRedirect: File) : this(command, nextId(), outputRedirect)

    companion object {
        private var currentId = 0
        private fun nextId() = currentId++
    }


    /**
     * Run `this` command and wait for it. This method is thread-blocking
     * @param workingDir working directory for `this` command
     * @param timeout time-out for `this` command. If null, no time-out applied
     */
    fun run(
        workingDir: File = currentWorkingDir(),
        timeout: TimeOut?,
    ): CommandResult {
        val process = ProcessBuilder(command)
            .directory(workingDir.absoluteFile)
            .redirectOutput(ProcessBuilder.Redirect.to(outputRedirect))
            .redirectError(ProcessBuilder.Redirect.to(outputRedirect))
            .start()
        if (timeout != null){
            process.waitFor(timeout.time, timeout.timeUnit)
        } else {
            process.waitFor()
        }
        return CommandResult(
            process.exitValue(),
            outputRedirect.readText()
        )
    }

    /**
     * Result of command run
     * @param exitCode exit code that describes command launch. 0 means that process is finished successfully, non-zero code means that error occurred during command run
     * @param commandOutput command output, i.e. stack trace, verbose message, etc.
     */
    data class CommandResult(
        val exitCode: Int,
        val commandOutput: String,
    ) {

        /**
         * Exit application if exit code of a command is non-zero
         */
        fun exitIfNotZero(): Nothing? {
            if (exitCode != 0) {
                exitProcess(exitCode)
            }
            return null
        }

        /**
         * Throw a throwable if exit code is non-zero
         * @param throwable throwable to throw if exit code is non-zero
         */
        fun throwIfNotZero(throwable: Throwable): Nothing? {
            if (exitCode != 0) {
                throw throwable
            }
            return null
        }

        /**
         * Print output of a command into system's default output (System.out)
         */
        fun printOutput() {
            println(commandOutput)
        }
    }
}

/**
 * Launch command described by `this` String. This extension is thread-blocking
 * @param workingDir working directory for command described by `this` string
 * @param timeout time-out for command described by `this` string. If null, no time-out applied
 * @see Command.run
 * @see Command
 */

fun String.runCommand(
    workingDir: File = currentWorkingDir(),
    timeout: TimeOut? = null
): Command.CommandResult = Command(this).run(workingDir, timeout)

/**
 * Launch command described by `this` list of strings. This extension is thread-blocking
 * @param workingDir working directory for command described by `this` string
 * @param timeout time-out for command described by `this` string. If null, no time-out applied
 * @see Command.run
 * @see Command
 */
fun List<String>.runCommand(
    workingDir: File = currentWorkingDir(),
    timeout: TimeOut? = null
): Command.CommandResult = Command(this).run(workingDir, timeout)
