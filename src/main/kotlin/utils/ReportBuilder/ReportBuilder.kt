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
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe
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
                    p { +message }
                }
            }
        }
    }
}

private class Warning(name: String, message: String): Card(name, message, ReportType.WARNING)
private class Error(name: String, message: String): Card(name, message, ReportType.ERROR)
private class Success(name: String, message: String): Card(name, message, ReportType.SUCCESS)
private class Normal(name: String, message: String): Card(name, message, ReportType.NORMAL)

class ReportBuilder(val reportTitle: String = "Run-Report") {
    private val log: MutableList<Card> = mutableListOf()

    fun addWarning(title: String, message: String) {
        log.add(Warning(title, message))
    }

    fun addSuccess(title: String, message: String) {
        log.add(Success(title, message))
    }

    fun addNormalMessage(title: String, message: String) {
        log.add(Normal(title, message))
    }

    fun addError(title: String, message: String) {
        log.add(Error(title, message))
    }

    fun printReport() {
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

//                repeat(3) { index ->
//                    div("section") {
//                        val contentId = "section-content-$index"
//                        span("toggle-button") {
//                            onClick = "toggle('$contentId')"
//                            +"Toggle Section ${index + 1}"
//                        }
//                        div("content") {
//                            id = contentId
//                            p { +"This is the content of section ${index + 1}." }
//                        }
//                    }
//                }
            }
        }

        Path("src/test/resources/report.html").parent.createDirectories()
        Path("src/test/resources/report.html").writeText(html)
        println("Open report: ${Path("src/test/resources/report.html").absolutePathString()}")
    }
}