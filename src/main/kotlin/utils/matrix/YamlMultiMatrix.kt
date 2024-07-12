@file: Suppress("MaximumLineLength")

package utils.matrix

import Decodable
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

typealias YamlMap = Map<TransportType, WeekMap>
typealias TransportType = String
typealias WeekMap = Map<WeekSpecifier, DayMap>
typealias WeekSpecifier = String
typealias DayMap = Map<DaySpecifier, TimeMap>
typealias DaySpecifier = String
typealias TimeMap = Map<TimeSpecifier, ParserMap>
typealias TimeSpecifier = String
typealias ParserMap = Map<ParserSpecifier, Any>
typealias ParserSpecifier = String

/**
 * An concrete implementation of the MultiMatrix, which gets its matrices from a Yaml file. The Yaml file must look like this:
 *
 * On the top level there must be a mode coded in string, here for example bikesharing. On the next level
 * are the weeks. Valid options are "all weeks", or "week" followed by a comma-separated list of weeks e.g. "week 0,1,4"
 * On the next level are the days of the week. Here this specific entry of a concrete week or weeks overwrites the general entry all weeks.
 * Valid options are monday, tuesday, ..., sunday, weekday, everyday. Here, weekday overwrites everyday on the
 * weekdays (Mon-Fri) and a specific weekday e.g. monday overwrites weekday and everyday. On the next
 * level are the times. These are inclusive to exclusive.
 * On the next level there must be a parser entry followed by the path of the matrix. The parser must be a string representation of an available
 * parser (i.e. a variant of MatrixImpl).  If the same parser and the same path are used for two different entries, the
 * matrix is still only loaded once and reused to save computing time and memory.
 *
 * Example:
 *
 * bikesharing:
 * all weeks:
 * monday:
 * 0:00 to 12:00:
 * visum_matrix: resources/visum_parser/bikesharing_always_monday_between_0_and_12.mtx
 * saturday:
 * 0:00 to 24:00:
 * visum_matrix: resources/visum_parser/bikesharing_always_saturday_the_entire_day.mtx
 * sunday:
 * 0:00 to 24:00:
 * visum_matrix: resources/visum_parser/bikesharing_always_sunday_the_entire_day.mtx
 * week 0,1:
 * weekday:
 * 0:00 to 24:00:
 * visum_matrix: resources/visum_parser/bikesharing_weekdays_in_week_0_and_1_the_entire_day.mtx
 * week 1:
 * sunday:
 * 0:00 to 24:00:
 * visum_matrix: resources/visum_parser/bikesharing_sunday_in_week_1_the_entire_day.mtx
 * car:
 * all weeks:
 * everyday:
 * 0:00 to 24:00:
 * visum_matrix: resources/visum_parser/default_car_matrix.mtx
 * sunday:
 * 0:00 to 24:00:
 * visum_matrix: resources/visum_parser/car_always_sunday_the_entire_day.mtx
 *
 *
 * M: Mode enum
 * I: Origin Destination enum
 * O: Output for example Distance
 * parser: converts the double from the matrix into the concrete output type, e.g. costs, travel time, ...
 * modeDecoder: converts the ModeString from the Yaml into a mode
 * path: path of the Yaml file
 */
class YamlMultiMatrix<M, I, O>(path: Path, parser: (Double) -> O, modeDecoder: Decodable<M>) : MultiMatrix<M, I, O> {

    // a map from Mode to a list of matrices
    // The list of matrices are sorted in time, each pair consisting of the start time and the matrix valid from
    // this time valid matrix. As soon as the next entry begins, the previous one ends
    private val matrixMap: Map<M, List<Pair<MyTempAbsoluteTime, Matrix<I, O>>>> =
        YamlMultiMatrixParser<M, I, O>(path, parser, modeDecoder).getMatrix()

    override operator fun get(mode: M, time: TempAbsoluteTime): Matrix<I, O> {
        val matrixList = matrixMap[mode]?.toMutableList() ?: throw IllegalArgumentException("Mode $mode not found")
        var index = 0

        // Increase index as long as the next entry has already started
        while (matrixList.size > index + 1 && matrixList[index + 1].first < time) {
            index += 1
        }

        // Now return the matrix that started last
        return matrixList[index].second
    }
}

/**
 * The parser of the Yaml files is done in three phases. In the first, the file is converted into a structure of nested maps
 * using an external parser. Then, in the second phase, a list of entries is generated from the nested maps. In the third
 * phase, these entries are converted into a sorted list of times and matrices. These three phases are carried out for
 * each transport type. At the end, the transport types and the corresponding list of matrices with timestamps are packed
 * into a map and returned
 */
private class YamlMultiMatrixParser<M, I, O>(
    private val path: Path,
    private val parser: (Double) -> O,
    private val modeDecoder: Decodable<M>
) {
    private val entryList = mutableListOf<Pair<TransportType, Entry>>()

    fun getMatrix(): Map<M, List<Pair<MyTempAbsoluteTime, Matrix<I, O>>>> {
        createEntries()
        val entriesMap = entryList.groupBy({ it.first }, { it.second })

        val matrixMap = mutableMapOf<M, List<Pair<MyTempAbsoluteTime, Matrix<I, O>>>>()

        // Process each entry list
        entriesMap.forEach { (key, entries) ->
            val mode = modeDecoder.decode(key)
            val matrixList = processEntries(entries, parser)
            matrixMap[mode] = matrixList
        }

        return matrixMap
    }

    private fun createEntries() {
        // Open the YAML file
        val yamlFile = path.toFile()

        // Create a Yaml instance
        val yaml = Yaml()

        // Read the YAML file into a Map
        val yamlMap: YamlMap = yaml.load(
            yamlFile.inputStream()
        )

        yamlMap.forEach { (transportType, weekMap) ->
            createEntries(transportType, weekMap)
        }
    }

    private fun createEntries(transportType: TransportType, weekMap: WeekMap) {
        val simulationInclusiveStart = getSimulationInclusiveStart()
        val simulationExclusiveEnd = getSimulationExclusiveEnd()

        weekMap.forEach { (weekSpec, dayMap) ->
            var increaseLevel = false
            val weeks = if (weekSpec == "all weeks") {
                (simulationInclusiveStart.week until simulationExclusiveEnd.week + 1).toList()
            } else {
                increaseLevel = true
                weekSpec.split(",").map { it.trim().removePrefix("week ").toInt() }
            }

            createEntries(transportType, increaseLevel, weeks, dayMap)
        }
    }

    private fun createEntries(transportType: TransportType, increaseLevel: Boolean, weeks: List<Int>, dayMap: DayMap) {
        dayMap.forEach { (daySpecifier, timeMap) ->
            // Use getDays() method to get a list of days
            var (days, level) = DayIdentifier.fromString(daySpecifier).getDays()
            // increase the level by half a step if the entry should only apply in certain weeks so that it can overwrite an entry that applies in all weeks
            @Suppress("MagicNumber")
            level *= 10
            if (increaseLevel) {
                @Suppress("MagicNumber")
                level += 5
            }

            createEntries(transportType, days, weeks, level, timeMap)
        }
    }
    private fun createEntries(
        transportType: TransportType,
        days: List<TempWeekday>,
        weeks: List<Int>,
        level: Int,
        timeMap: TimeMap
    ) {
        timeMap.forEach { (timeSpecifier, details) ->
            val (startTime, endTime) = timeSpecifier.split(" to ")
            val (startHour, startMinute) = startTime.split(":").map { it.toInt() }
            val (endHour, endMinute) = endTime.split(":").map { it.toInt() }

            // Find the parser key dynamically
            val parser = details.keys.firstOrNull() ?: throw IllegalArgumentException("No parser key found in $details")
            val path = details[parser] as String

            weeks.forEach { week ->
                days.forEach { individualDay ->
                    val includedStartTime =
                        MyTempAbsoluteTime(individualDay, startHour, startMinute, 0, week)
                    val excludedEndTime = MyTempAbsoluteTime(individualDay, endHour, endMinute, 0, week)

                    entryList.add(
                        transportType to Entry(
                            includedStartTime = includedStartTime,
                            excludedEndTime = excludedEndTime,
                            level = level,
                            path = path,
                            parser = MatrixImpl.fromString(parser)
                        )
                    )
                }
            }
        }
    }

    // Here the individual entries describing related time periods are converted into a list of matrices converted
    @Suppress("CyclomaticComplexMethod", "CognitiveComplexMethod")
    private fun <O> processEntries(
        entries: List<Entry>,
        converter: (Double) -> O
    ): List<Pair<MyTempAbsoluteTime, Matrix<I, O>>> {
        val simulationInclusiveStart = getSimulationInclusiveStart()
        val simulationExclusiveEnd = getSimulationExclusiveEnd()

        class EntryWrapper<C>(val entry: Entry, val keyExtract: (Entry) -> C) : Comparable<EntryWrapper<C>> where C : Comparable<C> {
            fun getC(): C {
                return keyExtract(entry)
            }

            override fun compareTo(other: EntryWrapper<C>): Int {
                return this.getC().compareTo(other.getC())
            }
        }

        val futureEntries = PriorityQueue<EntryWrapper<MyTempAbsoluteTime>>()
        val activeEntries = PriorityQueue<EntryWrapper<MyTempAbsoluteTime>>()

        entries.forEach {
            futureEntries.add(EntryWrapper(it) { it.includedStartTime })
        }

        val matrixMap = HashMap<Pair<String, MatrixImpl>, Matrix<I, O>>()
        val matrixList = ArrayList<Pair<MyTempAbsoluteTime, Matrix<I, O>>>()

        var currentTime = simulationInclusiveStart
        // iterate over the entries to find the one that is active at a given time
        while (currentTime < simulationExclusiveEnd) {
            // pull new started entries into the activeEntries
            while (futureEntries.isNotEmpty() && futureEntries.peek().entry.includedStartTime == currentTime) {
                activeEntries.add(EntryWrapper(futureEntries.poll().entry) { it.excludedEndTime })
            }

            // remove the ended entries from the activeEntries
            while (activeEntries.isNotEmpty() && activeEntries.peek().entry.excludedEndTime <= currentTime) {
                activeEntries.poll()
            }

            val currentActiveEntry = activeEntries.maxByOrNull { it.entry.level }

            check(currentActiveEntry != null) { "No entry covers time $currentTime" }

            val entry = currentActiveEntry.entry
            val path = entry.path
            val parser = entry.parser
            val matrix = matrixMap.getOrPut(path to parser) {
                parser.getMatrix(Path.of(path), converter)
            }

            if (matrixList.isEmpty() || matrixList.last().second != matrix) {
                matrixList.add(currentTime to matrix)
            }

            currentTime = if (activeEntries.isNotEmpty()) {
                listOfNotNull(
                    activeEntries.peek()?.getC(),
                    futureEntries.peek()?.getC()
                ).minOrNull() ?: simulationExclusiveEnd
            } else {
                futureEntries.peek()?.getC() ?: simulationExclusiveEnd
            }
        }
        return matrixList
    }
}

/*
 * The level determines which identifiers can overlay other identifiers. Higher level overrides lower level
 */
data class Entry(
    val includedStartTime: MyTempAbsoluteTime,
    val excludedEndTime: MyTempAbsoluteTime,
    val path: String,
    val level: Int,
    val parser: MatrixImpl,
) : Comparable<Entry> {
    override fun compareTo(other: Entry): Int {
        return compareValuesBy(this, other, Entry::includedStartTime, Entry::level)
    }
}

enum class DayIdentifier {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday,
    Weekday,
    Everyday;

    /*
     * The level determines which identifiers can overlay other identifiers. Higher level overrides lower level
     */
    fun getDays(): Pair<List<TempWeekday>, Int> {
        val weekdays = when (this) {
            Monday -> listOf(TempWeekday.MONDAY)
            Tuesday -> listOf(TempWeekday.TUESDAY)
            Wednesday -> listOf(TempWeekday.WEDNESDAY)
            Thursday -> listOf(TempWeekday.THURSDAY)
            Friday -> listOf(TempWeekday.FRIDAY)
            Saturday -> listOf(TempWeekday.SATURDAY)
            Sunday -> listOf(TempWeekday.SUNDAY)
            Weekday -> listOf(
                TempWeekday.MONDAY,
                TempWeekday.TUESDAY,
                TempWeekday.WEDNESDAY,
                TempWeekday.THURSDAY,
                TempWeekday.FRIDAY
            )
            Everyday -> TempWeekday.entries
        }

        val level = when (this) {
            Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday -> 2
            Weekday -> 1
            Everyday -> 0
        }

        return Pair(weekdays, level)
    }

    companion object {
        fun fromString(value: String): DayIdentifier {
            return when (value.lowercase(Locale.getDefault())) {
                "monday" -> Monday
                "tuesday" -> Tuesday
                "wednesday" -> Wednesday
                "thursday" -> Thursday
                "friday" -> Friday
                "saturday" -> Saturday
                "sunday" -> Sunday
                "weekday" -> Weekday
                "everyday" -> Everyday
                else -> throw IllegalArgumentException("Unknown day: $value")
            }
        }
    }
}

enum class MatrixImpl {
    VisumMatrix;

    fun <I, O> getMatrix(path: Path, converter: (Double) -> O): Matrix<I, O> {
        return when (this) {
            VisumMatrix -> {
                val matrix: Matrix<*, O> = VisumMatrix(path, converter)
                // The following suppresses the unchecked cast warning because we are unable to verify it at compile time due to the dynamic nature of this cast.
                // Unfortunately, Kotlin does not provide a more elegant solution for this scenario.
                @Suppress("UNCHECKED_CAST")
                checkNotNull(matrix as? Matrix<I, O>) {
                    "Invalid type for VisumMatrix"
                }
            }
        }
    }

    companion object {
        fun fromString(value: String): MatrixImpl {
            return when (value.lowercase(Locale.getDefault())) {
                "visum_matrix" -> VisumMatrix
                else -> throw IllegalArgumentException("Unknown parser: $value")
            }
        }
    }
}

@Suppress("MagicNumber")
fun getSimulationInclusiveStart() = MyTempAbsoluteTime(TempWeekday.MONDAY, 0, 0, 0, 0)

@Suppress("MagicNumber")
fun getSimulationExclusiveEnd() = MyTempAbsoluteTime(TempWeekday.MONDAY, 0, 0, 0, 4)
