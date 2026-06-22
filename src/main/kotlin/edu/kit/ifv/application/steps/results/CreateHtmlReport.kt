package edu.kit.ifv.application.steps.results
import edu.kit.ifv.application.steps.ResultsConfig
import edu.kit.ifv.core.modelsteps.Context
import edu.kit.ifv.core.modelsteps.steps.modelStep
import java.nio.file.Path

/**
 * [modelStep] to create a HTML file from the receiver [Context]'s report.
 *
 * @receiver context with a report
 * @param outputDir directory where HTML report file should be saved. Defaults to [config].resultDir
 * @param config configuration context parameter with a resultDir
 */
context(config: ResultsConfig)
fun Context.createHtmlReport(outputDir: Path = config.resultDir) = modelStep("create HTML report") {
    report.writeHtmlReport(outputDir)
    // TODO add option to automatically open in browser?
}
