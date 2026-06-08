package edu.kit.ifv.utils.report
import edu.kit.ifv.utils.files.toValidFileName
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
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

/**
 * This class provides the functionality to log messages and print a html report out of them.
 *
 * Add items to the overview card at the top with `addOverviewItem`
 *
 * Add logs and messages to the output via the `add[...]Log(...)` functions.
 *
 * Create the report via the `printReport(...)` function.
 *
 * The Logs will be sorted by recency, and the following order:
 * 1. Errors
 * 2. Successes
 * 3. Warnings
 * 4. Normal-Logs
 *
 * The overview items are sorted by recency.
 * */
class ReportBuilder(val reportTitle: String = "Run-Report") {

    private val quickOverview: OverviewCard by lazy { OverviewCard() }
    private val errors: MutableList<Error> = mutableListOf()
    private val success: MutableList<Success> = mutableListOf()
    private val warnings: MutableList<Warning> = mutableListOf()
    private val normals: MutableList<Normal> = mutableListOf()

    fun <R> detectReportChanges(scope: () -> R): ReportDiff<R> {
        val initOverviews = quickOverview.size
        val initErrors = errors.size
        val initSuccess = success.size
        val initWarnings = warnings.size
        val initNormals = normals.size

        val result = scope()

        return ReportDiff(
            result,
            newOverviews = (quickOverview.size - initOverviews) > 0,
            newErrors = (errors.size - initErrors) > 0,
            newSuccess = (success.size - initSuccess) > 0,
            newWarnings = (warnings.size - initWarnings) > 0,
            newNormals = (normals.size - initNormals) > 0,
        )
    }

    /**
     * Adds a new item to the overview list at the top.
     * @param name The name of the item.
     * @param status Whether the item succeeded, failed or produced a warning.
     * @param hoverInformation Any information that should be displayed, when hovering over this item. Useful for
     * giving a brief explanation for what went wrong in a step.
     */
    fun addOverviewItem(name: String, status: CardStatus, hoverInformation: String = "") {
        quickOverview.addOverviewItem(name, status, hoverInformation)
    }

    /**
     * Adds an error card to the log section of the report.
     */
    fun addErrorLog(title: String, message: String) {
        errors.add(Error(title, message))
    }

    /**
     * Returns whether any error was logged.
     */
    fun hasErrors() = errors.isNotEmpty()

    /**
     * Adds a success card to the log section of the report.
     */
    fun addSuccessLog(title: String, message: String) {
        success.add(Success(title, message))
    }

    /**
     * Adds a warning card to the log section of the report.
     */
    fun addWarningLog(title: String, message: String) {
        warnings.add(Warning(title, message))
    }

    /**
     * Returns whether any warning was logged.
     */
    fun hasWarnings() = warnings.isNotEmpty()

    /**
     * Adds a normal (non highlighted) card to the log section of the report.
     */
    fun addNormalLog(title: String, message: String) {
        normals.add(Normal(title, message))
    }

    private fun List<ReportStandardCard>.printItemsGroupedByTitle() {
        val maxNameLength = maxOf { it.name.length }

        this.groupBy { it.name }.forEach { (name, list) ->

            if (list.size == 1) {
                val padding = " ".repeat(maxNameLength - name.length)
                println(" * $name$padding: ${list.first().message}")
            } else {
                println(" * $name")
                list.forEach {
                    println("    - ${it.message}")
                }
            }
        }
    }

    fun printToConsole() {
        println("Report: $reportTitle")
        quickOverview.printToConsole()
        if (errors.isNotEmpty()) {
            println("Errors:")
            errors.printItemsGroupedByTitle()
        }
        if (warnings.isNotEmpty()) {
            println("Warnings:")
            warnings.printItemsGroupedByTitle()
        }
        if (normals.isNotEmpty()) {
            println("Infos:")
            normals.printItemsGroupedByTitle()
        }
        if (success.isNotEmpty()) {
            println("Success:")
            success.printItemsGroupedByTitle()
        }
    }

    /**
     * Creates outputDir, if not already existing. Writes a [reportTitle].html file into that directory and prints it's
     * location onto the console.
     * The created report includes all events added up to this point.
     * @param outputDir directory where the HTML report file should be stored
     * @return the [Path] of the created HTML report file
     */
    @Suppress("CognitiveComplexMethod")
    fun writeHtmlReport(outputDir: Path): Path {
        val html = createHTML().html {
            head {
                title(reportTitle)
                style {
                    unsafe {
                        +readResource("edu/kit/ifv/utils/report/report.css")
                    }
                }
                script {
                    unsafe {
                        +readResource("edu/kit/ifv/utils/report/report.js")
                    }
                }
            }
            body {
                unsafe {
                    +createBody()
                }
            }
        }

        outputDir.createDirectories()
        val outputFile = outputDir.resolve("${reportTitle.toValidFileName()}.html")
        outputFile.writeText(html)
        val absolutePath = outputFile.absolute().normalize().toUri().toASCIIString()
        print("\n")
        println(BOLD + BLUE + "OPEN REPORT: " + absolutePath + RESET)
        print("\n")
        return outputFile
    }
    private fun readResource(path: String): String {
        return requireNotNull(ReportBuilder::class.java.classLoader.getResource(path)) {
            "Resource not found: $path"
        }.readText()
    }
    private fun createBody(): String = createHTML().div("main") {
        h1("title") {
            style = "color: var(--highlight-color)"
            +reportTitle
        }
        unsafe { +quickOverview.getHtml() }
        div("logs-card") {
            h3("logs heading") {
                style = "color: var(--normal-color)"
                +"Logs"
            }
            for (t in errors) {
                unsafe { +t.getHtml() }
            }
            for (t in success) {
                unsafe { +t.getHtml() }
            }
            for (t in warnings) {
                unsafe { +t.getHtml() }
            }
            for (t in normals) {
                unsafe { +t.getHtml() }
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
                    +DARKMODE_MOON_SVG.trimIndent()
                }
            }
        }
    }
}

data class ReportDiff<R>(
    val result: R,
    val newOverviews: Boolean,
    val newErrors: Boolean,
    val newSuccess: Boolean,
    val newWarnings: Boolean,
    val newNormals: Boolean,
)

private const val BLUE = "\u001B[34m"
private const val BOLD = "\u001B[1m"
private const val RESET = "\u001B[0m"

@Suppress("MaxLineLength", "MaximumLineLength", "TopLevelPropertyNaming")
private const val DARKMODE_MOON_SVG = """<path d="M3.32031 11.6835C3.32031 16.6541 7.34975 20.6835 12.3203 20.6835C16.1075 20.6835 19.3483 18.3443 20.6768 15.032C19.6402 15.4486 18.5059 15.6834 17.3203 15.6834C12.3497 15.6834 8.32031 11.654 8.32031 6.68342C8.32031 5.50338 8.55165 4.36259 8.96453 3.32996C5.65605 4.66028 3.32031 7.89912 3.32031 11.6835Z" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>"""
