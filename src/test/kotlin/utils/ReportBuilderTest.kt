package utils

import utils.ReportBuilder.ReportBuilder
import kotlin.test.Test

class ReportBuilderTest {
    @Test
    fun testReport() {
        val builder = ReportBuilder()
        builder.addWarning("Warning", "You overused the number of calls...")
        builder.addWarning("Too many parsing faults", "It occured, that class XYZ had too many parsing faults during execution of the long-term-module.")
        builder.addSuccess("Successfully completed the number of calls.",
            art)
        builder.addError("Error","oh noooo we failed")
        builder.addNormalMessage("Nothing happened", "Not even here")
        builder.printReport()
    }
}

val art = "⡿⡿⠟⠓⠛⠻⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
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