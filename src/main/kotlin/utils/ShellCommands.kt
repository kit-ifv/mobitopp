package utils

import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

fun String.runCommand(workingDir: File): String? {
    return try {
        val steps = this.split("|")


        val builders = steps.map {
            val parts = it.trim().split("\\s".toRegex())
            println(parts)
            ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
        }

        val pipeline = ProcessBuilder.startPipeline(builders)
        val last = pipeline.last()


        last.waitFor(60, TimeUnit.MINUTES)
        last.inputStream.bufferedReader().readText()
    } catch(e: IOException) {
        e.printStackTrace()
        null
    }
}

fun String.execRuntime(): String? {
    return try {
        val parts = this.split("\\s".toRegex())
        println(parts)


        val process = Runtime.getRuntime().exec(parts.toTypedArray())
        val result = process.waitFor()
        print(result)

        val stdOut = InputStreamReader(process.inputStream)
        val stdErr = InputStreamReader(process.errorStream)

        return (stdOut.readLines().joinToString(separator = "\n")
                + "\n"
                + stdErr.readLines().joinToString(separator = "\n"))

    } catch(e: IOException) {
        e.printStackTrace()
        null
    }
}