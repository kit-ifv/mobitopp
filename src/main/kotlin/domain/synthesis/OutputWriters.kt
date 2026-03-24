package domain.synthesis

import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path

data class OutputWriters(
    var householdWriter: Writer? = null,
    var personWriter: Writer? = null,
    var carWriter: Writer? = null,
    var activityWriter: Writer? = null,
    var fixedDestinationWriter: Writer? = null,
    var opportunitiesWriter: Writer? = null,
) {
    companion object {
        fun useDirectory(path: Path): OutputWriters {
            val outputWriters = OutputWriters()

            outputWriters.householdWriter = Files.newBufferedWriter(path.resolve("households.csv"))
            outputWriters.personWriter = Files.newBufferedWriter(path.resolve("person.csv"))
            return outputWriters
        }
    }
}