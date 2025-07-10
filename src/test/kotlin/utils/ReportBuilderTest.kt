package utils

import utils.report.ReportBuilder
import kotlin.io.path.Path

fun main() {
    val builder = ReportBuilder()
    builder.addWarning("Warning", "You overused the number of calls...")
    builder.addWarning(
        "Too many parsing faults",
        "It occured, that class XYZ had too many parsing faults during execution of the long-term-module."
    )
    builder.addSuccess(
        "Successfully completed the number of calls.",
        Art
    )
    builder.addError("Error", "oh noooo we failed")
    builder.addNormalMessage("Nothing happened", "Not even here")
    builder.printReport(Path("src/test/resources/"))
}

@Suppress("TopLevelPropertyNaming")
const val Art = "⡿⡿⠟⠓⠛⠻⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
    "⣷⣶⣾⣿⣷⣶⣤⣬⣟⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
    "⣿⣟⣱⡿⠿⠿⠿⣮⣻⣿⣿⣿⣿⣏⢿⣿⡿⢁⢀⣀⣀⣀⣬⣉⣙⠋⠛⠿⢿⣿\n" +
    "⣟⣛⣡⣤⣤⣁⣀⣄⣉⣻⣿⣿⣿⣿⠛⡿⠻⠛⠭⠿⡿⠯⣭⣟⡻⢿⣶⣦⣀⢙\n" +
    "⣿⣿⣿⣯⣝⣛⣛⣛⣭⣾⣿⣿⣿⢇⣨⢶⣿⣶⡾⢶⣶⡶⢤⣤⣤⣀⠟⢉⣛⣓\n" +
    "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⣿⠿⠻⠿⢿⣿⣷⣶⣮⣭⣭⣭⣴⣾⡻⣮⣵\n" +
    "⣿⣿⣿⣿⣿⠟⢋⣽⣿⣿⣿⣿⡿⢿⡿⠿⠿⣆⣉⠻⣿⣿⣿⣿⣿⣿⣿⣷⣽⣿\n" +
    "⣿⣿⣿⠟⢁⣶⣿⣿⣿⣿⣟⢋⣀⣒⣀⣐⣫⡍⠛⠿⠪⠻⣿⣿⣿⣿⣿⣿⣿⣿\n" +
    "⣿⣿⢏⠀⣿⣏⣠⣽⣍⣍⣡⣾⣿⣿⣟⣋⣋⣓⣓⣼⣥⣤⡈⠻⣿⣿⣿⡏⣿⣿\n" +
    "⣿⣿⡌⢦⢻⣮⡁⣼⣿⢭⣭⣉⣭⣭⣭⣉⣭⢛⡉⣛⢛⡛⠛⣠⡌⣿⣿⣧⣿⣿\n" +
    "⣙⢿⣿⡜⣿⣿⣷⡹⠿⠿⠿⠿⠿⠟⠿⠛⠿⠻⠿⠿⠿⢃⣴⣿⢣⣿⣿⣿⣿⣿\n" +
    "⣿⣿⣿⣿⡜⣿⣿⡗⠦⣀⣀⢀⠐⠒⠀⠀⠀⢀⡀⠀⣠⣾⣿⢏⣾⢏⢽⣻⣿⣿\n" +
    "⣿⣿⣿⠈⠙⣾⣏⢧⣼⣿⣟⣛⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣯⣼⡋⠿⣾⣿⣿⣾\n" +
    "⢿⣿⣿⠐⠄⠺⣿⣾⢟⣻⣿⣿⣛⡿⠿⢿⣾⣟⣭⡾⣿⣿⣿⡟⣿⣾⡻⣿⠿⠛\n" +
    "⠀⡹⣿⡆⠈⣠⣿⣷⣿⣿⣿⣿⣿⣿⣿⣷⣬⡛⣻⣿⣿⣿⣿⣹⢖⠝⠁⡳⣾⣾"
