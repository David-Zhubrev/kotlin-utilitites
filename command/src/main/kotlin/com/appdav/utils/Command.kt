package com.appdav.utils

import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

private fun currentWorkingDir() = File("")

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
     * @param timeout time-out for `this` command. Default value is 10 000 milliseconds (10 seconds)
     * @param timeoutUnits time units for the timeout value. Default value is 10 000 milliseconds (10 seconds)
     */
    fun run(
        workingDir: File = currentWorkingDir(),
        timeout: Long = 10_000L,
        timeoutUnits: TimeUnit = TimeUnit.MILLISECONDS,
    ): CommandResult {
        val process = ProcessBuilder(command)
            .directory(workingDir.absoluteFile)
            .redirectOutput(ProcessBuilder.Redirect.to(outputRedirect))
            .redirectError(ProcessBuilder.Redirect.to(outputRedirect))
            .start()
        process.waitFor(timeout, timeoutUnits)
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
        fun printOutput(){
            println(commandOutput)
        }
    }
}

/**
 * Launch command described by `this` String. This extension is thread-blocking
 * @param workingDir working directory for command described by `this` string
 * @param timeout time-out for command described by `this` string. Default value is 10 000 milliseconds (10 seconds)
 * @param timeoutUnits time units for the timeout value. Default value is 10 000 milliseconds (10 seconds)
 * @see Command.run
 * @see Command
 */
fun String.runCommand(
    workingDir: File = currentWorkingDir(),
    timeout: Long = 10_000L,
    timeoutUnits: TimeUnit = TimeUnit.MILLISECONDS,
): Command.CommandResult =
    Command(this).run(workingDir, timeout, timeoutUnits)

/**
 * Launch command described by `this` list of strings. This extension is thread-blocking
 * @param workingDir working directory for command described by `this` string
 * @param timeout time-out for command described by `this` string. Default value is 10 000 milliseconds (10 seconds)
 * @param timeoutUnits time units for the timeout value. Default value is 10 000 milliseconds (10 seconds)
 * @see Command.run
 * @see Command
 */
fun List<String>.runCommand(
    workingDir: File = currentWorkingDir(),
    timeout: Long = 10_000L,
    timeoutUnits: TimeUnit = TimeUnit.MILLISECONDS,
): Command.CommandResult = Command(this).run(workingDir, timeout, timeoutUnits)
