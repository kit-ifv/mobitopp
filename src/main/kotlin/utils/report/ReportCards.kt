package utils.report

import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.h5
import kotlinx.html.h6
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
            onClick = "event => {event.stopPropagation(); console.log(event)}"
            div("card " + type.cssClass) {
                onClick = "toggleCard('$contentID'); toggle('arrow $contentID', 'rotate')"
                span("card-top inline") {
                    unsafe {
                        +rotatableArrow("arrow $contentID")
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

/**
 * Creates an svg arrow.
 * @param classname additional css classnames, if needed.
 * @param contentID a, in the context of the document, unique identifier.
 */
fun rotatableArrow(contentID: String, classname:String = ""): String {
    return createHTML().svg(classes = "rotatable $classname") {
        id = contentID
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
 * It is collapsable, however it is open by default.
 */
internal class OverviewCard() {
    private val stepRegister: MutableList<StatusStep> = mutableListOf()

    /**
     * Adds a new item to this card.
     * @param name The name of the item.
     * @param status Whether the item succeeded, failed or produced a warning.
     * @param hoverInformation Any information that should be displayed, when hovering over this item. Useful for
     * giving a brief explanation for what went wrong in a step.
     */
    fun addOverviewItem(name: String, status: CardStatus, hoverInformation: String = "") {
        stepRegister.add(StatusStep(name, status, hoverInformation))
    }

    /**
     * @return the html string of this card.
     */
    fun getHtml(): String {
        if(stepRegister.isEmpty()) {
            return ""
        }
        val contentID = Random.nextInt().toString()
        return createHTML().span {
            div("overview-card") {
                onClick = "toggleBigCard('$contentID'); toggle('arrow $contentID', 'rotate')"
                h3("card-title") {
                    style = "color: var(--normal-color)"
                    pre {
                        +"Overview"
                    }
                }
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

private fun StatusStep.getSingleStepHTML(connectorEnabled: Boolean): String {
    return createHTML().div ("step") {
        attributes["title"] = hoverInformation
        span("flex-row-centered") {
            span {
                style = "color: var(--highlight-color);"
                unsafe {
                    +dotSVG
                }
                if (connectorEnabled) {
                    span {
                        style = "position: relative;width: 0;"
                        span {
                            style = "position: absolute; top: -75px;left: -12.5px;"
                            unsafe {
                                +Path("src/main/kotlin/utils/report/assets/Connector.svg").readText()
                            }
                        }
                    }
                }
            }
            h6 {
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

private fun statusIcon(status: CardStatus): String {
    return createHTML().span {
        when (status) {
            CardStatus.SUCCESS -> {
                style = "color: var(--success-color)"
                unsafe {
                    + Path("src/main/kotlin/utils/report/assets/Success_V3.svg").readText()
                }
            }
            CardStatus.WARNING -> {
                style = "color: var(--warning-color)"
                unsafe {
                    +Path("src/main/kotlin/utils/report/assets/Warning_V3.svg").readText()
                }
            }
            CardStatus.FAILURE -> {
                style = "color: var(--error-color);"
                unsafe {
                    +Path("src/main/kotlin/utils/report/assets/Failure_V3.svg").readText()
                }
            }
        }
    }
}
private val dotSVG = createHTML().svg(classes = "dot") {
    @Suppress("StringLiteralDuplication")
    attributes["fill"] = "currentColor"
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