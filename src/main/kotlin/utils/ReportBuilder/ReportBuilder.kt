package utils.ReportBuilder

import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.h5
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.onClick
import kotlinx.html.p
import kotlinx.html.pre
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe
import org.jetbrains.kotlinx.dataframe.api.toPath
import java.awt.Desktop
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.random.Random

private enum class ReportType(val cssClass: String) {
    NORMAL(""),
    SUCCESS("success"),
    WARNING("warning"),
    ERROR("error")
}
private abstract class Card (
    val name: String,
    val message: String,
    val type: ReportType
) {
    fun getHtml(): String {
        val contentID = Random.nextInt().toString()

        return createHTML().span {
            div("section " + type.cssClass) {
                onClick = "toggle('$contentID')"
                val contentId = "section-content-$contentID"
                span("toggle-button") {
                    h5 {
                        +name
                    }
                }
                div("content") {
                    id = contentID
                    pre { +message }
                }
            }
        }
    }
}

private class Warning(name: String, message: String): Card(name, message, ReportType.WARNING)
private class Error(name: String, message: String): Card(name, message, ReportType.ERROR)
private class Success(name: String, message: String): Card(name, message, ReportType.SUCCESS)
private class Normal(name: String, message: String): Card(name, message, ReportType.NORMAL)

/**
 * This class provides the functionality to log messages and print a html report out of them.
 */
class ReportBuilder(val reportTitle: String = "Run-Report") {
    private val log: MutableList<Card> = mutableListOf()

    /**
     * Adds a warning message to the report.
     */
    fun addWarning(title: String, message: String) {
        log.add(Warning(title, message))
    }

    /**
     * Adds a success message to the report.
     */
    fun addSuccess(title: String, message: String) {
        log.add(Success(title, message))
    }

    /**
     * Adds a normal (non highlighted) message to the report.
     */
    fun addNormalMessage(title: String, message: String) {
        log.add(Normal(title, message))
    }

    /**
     * Adds an error message to the report.
     */
    fun addError(title: String, message: String) {
        log.add(Error(title, message))
    }

    /**
     * Creates outputDir, if not already existing. Writes a [reportTitle].html file into that directory and prints it's
     * location onto the console.
     * The created report includes all events added up to this point.
     */
    fun printReport(outputDir: Path) {
        val html = createHTML().html {
            head {
                title(reportTitle)
                style {
                    unsafe {
                        + Path("src/main/kotlin/utils/ReportBuilder/report.css").readText()
                    }
                }
                script {
                    unsafe {
                        +"""
                        function toggle(id) {
                            const content = document.getElementById(id);
                            content.classList.toggle('show');
                        }
                    """.trimIndent()
                    }
                }
            }
            body {
                h1("title") {
                    +reportTitle
                }
                for (t in log) {
                    unsafe { +t.getHtml() }
                }
            }
        }

        outputDir.createDirectories()

        val outputFile = outputDir.resolve("$reportTitle.html")
        outputFile.writeText(html)
        val absolutePath = "file://" + outputFile.absolutePathString()
        println("Open report: " + absolutePath)
    }
}