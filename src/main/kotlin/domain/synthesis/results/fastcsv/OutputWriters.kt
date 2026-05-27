package domain.synthesis.results.fastcsv

import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories

data class OutputWriters(
    var householdWriter: Writer? = null,
    var personWriter: Writer? = null,
    var carWriter: Writer? = null,
    var activityWriter: Writer? = null,
    var fixedDestinationWriter: Writer? = null,
    var opportunitiesWriter: Writer? = null,
) {
    companion object {
        /**
         * Produces the directory and sets the writers to
         */
        fun useDirectoryForCSV(path: Path): OutputWriters {
            path.createDirectories() // Ensure the path exists
            val outputWriters = OutputWriters()

            outputWriters.householdWriter = Files.newBufferedWriter(path.resolve("households.csv"))
            outputWriters.personWriter = Files.newBufferedWriter(path.resolve("person.csv"))
            outputWriters.activityWriter = Files.newBufferedWriter(path.resolve("activities.csv"))

            return outputWriters
        }
    }
}
