package utils.report

import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.h5
import kotlinx.html.id
import kotlinx.html.onClick
import kotlinx.html.pre
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.svg
import kotlinx.html.unsafe
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.random.Random

internal enum class ReportType(val cssClass: String) {
    NORMAL("normal"),
    SUCCESS("success"),
    WARNING("warning"),
    ERROR("error")
}

internal abstract class ReportStandardCard(
    val name: String,
    val message: String,
    val type: ReportType
) {
    fun getHtml(): String {
        val contentID = Random.nextInt().toString()

        return createHTML().span {
            div("card " + type.cssClass) {
                onClick = "toggleCard('$contentID'); toggle('arrow $contentID', 'rotate')"
                span("card-top inline") {
                    svg(classes = "rotatable") {
                        id = "arrow $contentID"
                        @Suppress("StringLiteralDuplication")
                        attributes["stroke"] = "currentColor"
                        attributes["fill"] = "none"
                        @Suppress("StringLiteralDuplication")
                        attributes["width"] = "25"
                        @Suppress("StringLiteralDuplication")
                        attributes["height"] = "25"
                        @Suppress("StringLiteralDuplication")
                        attributes["viewbox"] = "0 0 20 12"
                        attributes["stroke-width"] = "3.0"
                        unsafe {
                            +"""
                            <path stroke-linecap="round" stroke-linejoin="round" d="M3 3 L10 10 L17 3"></path>
                            """.trimIndent()
                        }
                    }
                    h5("card-title") {
                        pre {
                            +name
                        }
                    }
                }
                div("card-body") {
                    id = contentID
                    pre("content") { +message }
                }
            }
        }
    }
}

internal class Warning(name: String, message: String) : ReportStandardCard(name, message, ReportType.WARNING)
internal class Error(name: String, message: String) : ReportStandardCard(name, message, ReportType.ERROR)
internal class Success(name: String, message: String) : ReportStandardCard(name, message, ReportType.SUCCESS)
internal class Normal(name: String, message: String) : ReportStandardCard(name, message, ReportType.NORMAL)

internal data class StatusStep (
    val name: String,
    val status: CardStatus,
    val hoverInformation: String
)

/**
 * Generic status enum used in the ReportBuilder framework.
 */
enum class CardStatus {
    SUCCESS,
    WARNING,
    FAILURE
}


/**
 * The Overview cards can visualize several steps and their status.
 */
class OverviewCard() {
    private val name = "Overview"
    private val stepRegister: MutableList<StatusStep> = mutableListOf()
    /**
     * Adds a new item to the list of steps.
     * @param name The name of the step.
     * @param status Whether the step succeeded, failed or produced a warning.
     * @param hoverInformation Any information that should be displayed, when hovering over this step. Useful for
     * giving a brief explanation for what went wrong in a step.
     */
    fun addStepStatus(name: String, status: CardStatus, hoverInformation: String = "") {
        stepRegister.add(StatusStep(name, status, hoverInformation))
    }


    /**
     * @return the html string of this card.
     */
    fun getHtml(): String {
        val contentID = Random.nextInt().toString()

        return createHTML().span {
            div("overview-card " + ReportType.NORMAL.cssClass) {
                h3("card-title") {
                    pre {
                        +name
                    }
                }
                div {
                    id = contentID
                    div("content") {
                        stepRegister.forEachIndexed { index, step ->
                            if (index == 0) {
                                unsafe {
                                    +step.getSingleStepHTML(false)
                                }
                            } else {
                                unsafe {
                                    + step.getSingleStepHTML(true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

internal fun StatusStep.getSingleStepHTML(connectorEnabled: Boolean): String {
    return createHTML().div ("step") {
        attributes["title"] = hoverInformation
        span("flex_row_centered") {
            unsafe {
                +dotSVG
            }
            if (connectorEnabled) {
                span {
                    style = "width: 0;color: var(--normal-color);position: relative; top: -28px;left: -30.5px;"
                    unsafe {
                        +Path("src/main/kotlin/utils/report/assets/Connector.svg").readText()
                    }
                }
            }
            h5 {
                style = "width: min-content;max-width: 80%; white-space: normal; text-wrap: nowrap; overflow:hidden; overflow-inline: auto;"
                +name
            }
            div ("line")
            unsafe {
                +statusIcon(status)
            }
        }
    }
}

fun statusIcon(status: CardStatus): String {
    return createHTML().span {
        when (status) {
            CardStatus.SUCCESS -> {
                style = "color: var(--success-color)"
                unsafe {
                    + Path("src/main/kotlin/utils/report/assets/Success.svg").readText()
                }
            }
            CardStatus.WARNING -> {
                style = "color: var(--warning-color)"
                unsafe {
                    +Path("src/main/kotlin/utils/report/assets/Warning.svg").readText()
                }
            }
            CardStatus.FAILURE -> {
                style = "color: var(--error-color);"
                unsafe {
                    +Path("src/main/kotlin/utils/report/assets/Failure.svg").readText()
                }
            }
        }
    }
}
private val dotSVG = createHTML().svg(classes = "dot") {
    @Suppress("StringLiteralDuplication")
    attributes["fill"] = "var(--normal-color)"
    @Suppress("StringLiteralDuplication")
    attributes["width"] = "20"
    @Suppress("StringLiteralDuplication")
    attributes["height"] = "20"
    @Suppress("StringLiteralDuplication")
    attributes["viewbox"] = "0 0 40 40"
    unsafe {
        +"""
        <ellipse cx="20" cy="20" rx="20" ry="20" stroke="none" pointer-events="all"/>
        """.trimIndent()
    }
}