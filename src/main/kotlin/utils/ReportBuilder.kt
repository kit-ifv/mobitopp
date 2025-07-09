package utils

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.writeText

class ReportBuilder {

    fun printReport() {
        val html = createHTML().html {
            head {
                title("Kotlin Report")
                style {
                    unsafe {
                        +"""
                        body {
                            font-family: sans-serif;
                            margin: 2em;
                            background-color: #f7f7f7;
                        }
                        .section {
                            border-radius: 10px;
                            background: white;
                            padding: 1em;
                            margin-bottom: 1em;
                            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                        }
                        .toggle-button {
                            cursor: pointer;
                            font-weight: bold;
                            color: #0057b7;
                            margin-bottom: 0.5em;
                            display: block;
                        }
                        .content {
                          max-height: 0;
                          overflow: hidden;
                          transition: max-height 0.2s ease;
                        }
                        
                        .content.show {
                          max-height: 500px; /* big enough for your content */
                        }
                        @keyframes fadeIn {
                            from { opacity: 0; }
                            to { opacity: 1; }
                        }
                    """.trimIndent()
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
                repeat(3) { index ->
                    div("section") {
                        val contentId = "section-content-$index"
                        span("toggle-button") {
                            onClick = "toggle('$contentId')"
                            +"Toggle Section ${index + 1}"
                        }
                        div("content") {
                            id = contentId
                            p { +"This is the content of section ${index + 1}." }
                        }
                    }
                }
            }
        }

        Path("src/test/resources/report.html").parent.createDirectories()
        Path("src/test/resources/report.html").writeText(html)
        println("Open report: ${Path("src/test/resources/report.html").absolutePathString()}")
    }
}