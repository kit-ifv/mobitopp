package utils.report

import kotlinx.html.div
import kotlinx.html.h5
import kotlinx.html.id
import kotlinx.html.onClick
import kotlinx.html.pre
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.svg
import kotlinx.html.unsafe
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
                onClick = "toggleCard('$contentID'); toggle('arrow ' + $contentID, 'rotate')"
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

class OverviewCard() {
    private val name = "Overview"
    fun getHtml(): String {
        val contentID = Random.nextInt().toString()

        return createHTML().span {
            div("card show " + ReportType.NORMAL.cssClass) {
                onClick = "toggleCard('$contentID'); toggle('arrow ' + $contentID, 'rotate')"
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
                    pre("content") { + "thjis is the content..."}
                }
            }
        }
    }
}