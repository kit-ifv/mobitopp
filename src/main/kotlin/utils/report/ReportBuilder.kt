package utils.report

import kotlinx.html.AreaShape
import kotlinx.html.Entities
import kotlinx.html.SVG
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h5
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.onClick
import kotlinx.html.pre
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.svg
import kotlinx.html.title
import kotlinx.html.unsafe
import org.jetbrains.kotlinx.kandy.letsplot.style.LayoutParameters.Companion.line
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
private abstract class Card(
    val name: String,
    val message: String,
    val type: ReportType
) {
    fun getHtml(): String {
        val contentID = Random.nextInt().toString()

        return createHTML().span {
            div("section " + type.cssClass) {
                onClick = "toggle('$contentID')"
                @Suppress("UnusedPrivateProperty")
                val contentId = "section-content-$contentID"
                span("toggle-button inline") {
                    unsafe {
                        +"""
                        <svg width="10" height="6" viewBox="0 0 10 6" xmlns="http://www.w3.org/2000/svg" stroke="currentColor" fill="none" stroke-width="1.5">
                          <path d="M1 1 L5 5 L9 1" stroke-linecap="round" stroke-linejoin="round" />
                        </svg>
                        """.trimIndent()
                    }
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

private class Warning(name: String, message: String) : Card(name, message, ReportType.WARNING)
private class Error(name: String, message: String) : Card(name, message, ReportType.ERROR)
private class Success(name: String, message: String) : Card(name, message, ReportType.SUCCESS)
private class Normal(name: String, message: String) : Card(name, message, ReportType.NORMAL)

/**
 * This class provides the functionality to log messages and print a html report out of them.
 *
 * Add cards and messages to the output via the add[...](...) functions.
 *
 * Create the report via the printReport(...) function.
 * */
class ReportBuilder(val reportTitle: String = "Run-Report") {
    private val log: MutableList<Card> = mutableListOf()

    /**
     * Adds a warning card to the report.
     */
    fun addWarning(title: String, message: String) {
        log.add(Warning(title, message))
    }

    /**
     * Adds a success card to the report.
     */
    fun addSuccess(title: String, message: String) {
        log.add(Success(title, message))
    }

    /**
     * Adds a normal (non highlighted) card to the report.
     */
    fun addNormalMessage(title: String, message: String) {
        log.add(Normal(title, message))
    }

    /**
     * Adds an error card to the report.
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
                        +Path("src/main/kotlin/utils/report/report.css").readText()
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
        print("\n\n")
        println(BOLD + BLUE + "OPEN RUN REPORT (ctrl + lmb): " + absolutePath + RESET)
        print("\n\n")
    }
}

// private const val RED = "\u001B[31m"
// private const val GREEN = "\u001B[32m"
private const val BLUE = "\u001B[34m"
private const val BOLD = "\u001B[1m"
private const val RESET = "\u001B[0m"
