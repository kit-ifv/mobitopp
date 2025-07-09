package utils

import kotlin.test.Test

class ReportBuilderTest {
    @Test
    fun testReport() {
        val builder = ReportBuilder()
        builder.printReport()
    }
}