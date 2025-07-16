package utils.report

import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.onClick
import kotlinx.html.script
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.svg
import kotlinx.html.title
import kotlinx.html.unsafe
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * This class provides the functionality to log messages and print a html report out of them.
 *
 * Add cards and messages to the output via the add[...](...) functions.
 *
 * Create the report via the printReport(...) function.
 *
 * The output will be sorted by recency, and the following order:
 * 1. Errors
 * 2. Warnings
 * 3. Successes
 * 4. Normal-Logs
 * */
class ReportBuilder(val reportTitle: String = "Run-Report") {

    private val warnings: MutableList<Warning> = mutableListOf()
    private val errors: MutableList<Error> = mutableListOf()
    private val success: MutableList<Success> = mutableListOf()
    private val normals: MutableList<Normal> = mutableListOf()
    private var quickOverview: OverviewCard? = null

    /**
     * Add overview items to the returned card. Only one overview card is possible.
     * @return the OverviewCard associated with this report. There can only be one card.
     */
    fun addOverview(): OverviewCard {
        if (quickOverview == null) {
            quickOverview = OverviewCard()
        }
        return quickOverview!!
    }

    /**
     * Adds a warning card to the report.
     */
    fun addWarning(title: String, message: String) {
        warnings.add(Warning(title, message))
    }

    /**
     * Adds a success card to the report.
     */
    fun addSuccess(title: String, message: String) {
        success.add(Success(title, message))
    }

    /**
     * Adds a normal (non highlighted) card to the report.
     */
    fun addNormalMessage(title: String, message: String) {
        normals.add(Normal(title, message))
    }

    /**
     * Adds an error card to the report.
     */
    fun addError(title: String, message: String) {
        errors.add(Error(title, message))
    }

    /**
     * Creates outputDir, if not already existing. Writes a [reportTitle].html file into that directory and prints it's
     * location onto the console.
     * The created report includes all events added up to this point.
     */
    @Suppress("CognitiveComplexMethod")
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
                        +Path("src/main/kotlin/utils/report/report.JS").readText()
                    }
                }
            }
            body {
                div("main") {
                    h1("title") {
                        style = "color: var(--highlight-color)"
                        +reportTitle
                    }
                    if (quickOverview!= null) unsafe { +quickOverview!!.getHtml() }
                    div("logs-card") {
                        h3("logs heading") {
                            style = "color: var(--normal-color)"
                            +"Logs"
                        }
                        for (t in errors) {
                            unsafe { +t.getHtml() }
                        }
                        for (t in warnings) {
                            unsafe { +t.getHtml() }
                        }
                        for (t in success) {
                            unsafe { +t.getHtml() }
                        }
                        for (t in normals) {
                            unsafe { +t.getHtml() }
                        }
                    }
                }
                button(classes = "darkmode-toggle") {
                    onClick = "toggleDarkMode()"
                    svg {
                        attributes["fill"] = "none"
                        attributes["stroke"] = "currentColor"
                        attributes["width"] = "30"
                        attributes["height"] = "30"
                        attributes["viewbox"] = "0 0 24 24"
                        unsafe {
                           +darkmodeMoonSVG.trimIndent()
                        }
                    }
                }
            }
        }

        outputDir.createDirectories()

        val outputFile = outputDir.resolve("$reportTitle.html")
        outputFile.writeText(html)
        val absolutePath = "file://" + outputFile.absolutePathString()
        print("\n")
        println(BOLD + BLUE + "RUN REPORT: " + absolutePath + RESET)
        print("\n")
    }
}

// private const val RED = "\u001B[31m"
// private const val GREEN = "\u001B[32m"
private const val BLUE = "\u001B[34m"
private const val BOLD = "\u001B[1m"
private const val RESET = "\u001B[0m"
@Suppress("MaximumLineLength")
private const val darkmodeMoonSVG = """<path d="M3.32031 11.6835C3.32031 16.6541 7.34975 20.6835 12.3203 20.6835C16.1075 20.6835 19.3483 18.3443 20.6768 15.032C19.6402 15.4486 18.5059 15.6834 17.3203 15.6834C12.3497 15.6834 8.32031 11.654 8.32031 6.68342C8.32031 5.50338 8.55165 4.36259 8.96453 3.32996C5.65605 4.66028 3.32031 7.89912 3.32031 11.6835Z" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>"""
