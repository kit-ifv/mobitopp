@file: Suppress("MaximumLineLength")

package core.datastructure.matrix

import domain.shared.datastructure.matrix.YamlInfo
import domain.shared.datastructure.matrix.visum.VisumMatrix
import domain.shared.location.ZoneId
import me.tongfei.progressbar.ProgressBar
import org.yaml.snakeyaml.Yaml
import utils.Decodable
import utils.Encodable
import utils.collections.defaultProgressBarBuilder
import utils.collections.stepBy
import utils.units.AbsoluteTime
import java.nio.file.Path
import java.time.DayOfWeek
import java.util.Locale
import java.util.PriorityQueue
import kotlin.io.path.pathString

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

class YamlMultiMatrixError(
    message: String,
    path: Path,
    cause: Throwable? = null,
) : Exception("Error in matrix configuration yaml file ${path.fileName} $message.\nSource: $path", cause)

//class YamlMatrixWrapper<M : Encodable>(
//    private val original: YamlMultiMatrix<M, ZoneId>,
//) : ZoneMatrixLookup<M> {
//    override fun get(mode: M, time: AbsoluteTime): ZoneIdMatrix {
//        return original[mode, time].toConcreteMatrix()
//    }
//}
//
//

fun interface MatrixCreationInstruction<I> {
    fun createMatrix(yamlInfo: YamlInfo): IndexedDoubleMatrix<I>
}

fun interface ZoneMatrixCreation : MatrixCreationInstruction<ZoneId> {
    override fun createMatrix(yamlInfo: YamlInfo): ZoneIdMatrix
}

val VISUM_PARSE = ZoneMatrixCreation {
    VisumMatrix(it.path)
}

val DEFAULT_PARSE = ZoneMatrixCreation {
    when(it.parserDescription) {
        "visum_matrix" -> VisumMatrix(it.path)
        "constant_matrix" -> ConstantZoneIdMatrix(it.path.toString().toDouble())
        else -> throw NoSuchElementException()
    }
}

abstract class MatrixYamlStructure<M> {
    abstract fun getInstructions(mode: M, time: AbsoluteTime): Path
}


/**
 * A concrete implementation of the MultiMatrix, which gets its matrices from a Yaml file. The Yaml file must look like this:
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
@Suppress("LongParameterList")
class YamlMultiMatrix<M, I>(
    path: Path,
    modeDecoder: Decodable<M>,
    simulationStartInclusive: AbsoluteTime,
    simulationEndExclusive: AbsoluteTime,
    formats: Collection<MatrixFormat<I>>, // TODO default value?
) : MatrixLookup<M, I> where M : Encodable {

    // a map from Mode to a list of matrices
    // The list of matrices are sorted in time, each pair consisting of the start time and the matrix valid from
    // this time valid matrix. As soon as the next entry begins, the previous one ends
    private val matrixMap: Map<M, List<Pair<AbsoluteTime, IndexedDoubleMatrix<I>>>> =
        YamlMultiMatrixParser<M, I>(
            path,
            modeDecoder,
            simulationStartInclusive,
            simulationEndExclusive,
            formats,
//            betterFormatFolder
        ).getMatrix()

    override operator fun get(mode: M, time: AbsoluteTime): IndexedDoubleMatrix<I> {
        return cache.evaluate(mode, time)
    }

    private val cache = MatrixCache()

    private inner class MatrixCache {
        private val rangeList: Map<M, Map<OpenEndRange<AbsoluteTime>, IndexedDoubleMatrix<I>>> = matrixMap.map { (k, v) ->
            k to v.zipWithNext { a, b ->
                val (start, matrix) = a
                val (secondStart, secondMatrix) = b
                Pair(start..<secondStart, matrix)
            }.associate { it.first to it.second }
        }.associate { it.first to it.second }

        private val cache: MutableMap<M, Triple<AbsoluteTime, AbsoluteTime, IndexedDoubleMatrix<I>>> = mutableMapOf()

        fun evaluate(mode: M, time: AbsoluteTime): IndexedDoubleMatrix<I> {
            val currentMatrix = cache[mode]?.takeIf { it.second > time }?.third ?: update(mode, time)

            return currentMatrix
        }

        fun update(mode: M, time: AbsoluteTime): IndexedDoubleMatrix<I> {
            val allMatricesForMode = requireNotNull(rangeList[mode]) {
                "The mode $mode has no matrices"
            }
            val last = matrixMap[mode]?.last()?.let { it.first..<AbsoluteTime.INFINITY to it.second }
                ?: throw NoSuchElementException("There should be a last matrix")
            val matrixTemp = allMatricesForMode.entries.firstOrNull { time in it.key }?.toPair() ?: last
//            val matrix = allMatricesForMode.findLast {
//                it.first < time
//            } ?: allMatricesForMode.last()
            cache[mode] = Triple(matrixTemp.first.start, matrixTemp.first.endExclusive, matrixTemp.second)
            return matrixTemp.second
        }
    }
}


/**
 * The parser of the Yaml files is done in three phases. In the first, the file is converted into a structure of nested maps
 * using an external parser. Then, in the second phase, a list of entries is generated from the nested maps. In the third
 * phase, these entries are converted into a sorted list of times and matrices. These three phases are carried out for
 * each transport type. At the end, the transport types and the corresponding list of matrices with timestamps are packed
 * into a map and returned
 */
@Suppress("LongParameterList")
private class YamlMultiMatrixParser<M, I>(
    private val path: Path,
    private val modeDecoder: Decodable<M>,
    private val simulationStartInclusive: AbsoluteTime,
    private val simulationEndExclusive: AbsoluteTime,
    private val formats: Collection<MatrixFormat<I>>, // TODO default value?
) where M : Encodable {
    private val entryList = mutableListOf<Pair<TransportType, Entry>>()
    val matrixMapCache = HashMap<Pair<String, MatrixFormat<I>>, IndexedDoubleMatrix<I>>()

    fun getMatrix(): Map<M, List<Pair<AbsoluteTime, IndexedDoubleMatrix<I>>>> {
        createEntries()
        val entriesMap = entryList.groupBy({ it.first }, { it.second })

        val matrixMap = mutableMapOf<M, List<Pair<AbsoluteTime, IndexedDoubleMatrix<I>>>>()

        // Process each entry list, show progress on progress bar
        val progressBar = progressBar(entriesMap)

        entriesMap.forEach { (key, entries) ->
            val mode = modeDecoder.decode(key)

            val matrixList: List<Pair<AbsoluteTime, IndexedDoubleMatrix<I>>>
            try {
                matrixList = processEntries(entries, mode)
            } catch (e: java.lang.IllegalArgumentException) {
                throw YamlMultiMatrixError(e.message ?: "", path, e)
            }

            progressBar.stepBy(entries.size)

            matrixMap[mode] = matrixList
        }

        return matrixMap
    }

    private fun progressBar(entriesMap: Map<TransportType, List<Entry>>): ProgressBar =
        defaultProgressBarBuilder(
            label = "Process ${path.fileName} matrices",
            expectedCount = entriesMap.map { it.value.size }.sum().toLong()
        ).build()

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
        weekMap.forEach { (weekSpec, dayMap) ->
            var increaseLevel = false
            val weeks = if (weekSpec == "all weeks") {
                (simulationStartInclusive.week until simulationEndExclusive.week + 1).toList()
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
            level *= 10// TODO can be inlined into dayIdentifier
            if (increaseLevel) {
                @Suppress("MagicNumber")
                level += 5 // TODO Can also be inlined into the weekspec if it were an enum instead of a raw string.
            }

            createEntries(transportType, days, weeks, level, timeMap)
        }
    }

    private fun createEntries(
        transportType: TransportType,
        days: List<DayOfWeek>,
        weeks: List<Int>,
        level: Int,
        timeMap: TimeMap,
    ) {
        timeMap.forEach { (timeSpecifier, details) ->
            val (startTime, endTime) = timeSpecifier.split(" to ")
            val (startHour, startMinute) = startTime.split(":").map { it.toInt() }
            val (endHour, endMinute) = endTime.split(":").map { it.toInt() }

            // Find the parser key dynamically
            val parser = details.keys.firstOrNull() ?: throw YamlMultiMatrixError(
                "No parser key found in $details",
                path
            )
            val path = resolvePathIfRelative(details[parser] as String)

            weeks.forEach { week ->
                days.forEach { individualDay ->
                    val includedStartTime = AbsoluteTime(individualDay, startHour, startMinute, 0, week)
                    val excludedEndTime = AbsoluteTime(individualDay, endHour, endMinute, 0, week)

                    entryList.add(
                        transportType to Entry(
                            includedStartTime = includedStartTime,
                            excludedEndTime = excludedEndTime,
                            level = level,
                            path = path,
                            parser = formats.fromString(parser, path)
                        )
                    )
                }
            }
        }
    }

    /**
     * Resolve the given path string.
     * If it starts with a "$" character, it is interpreted as a relative path
     * with respect to the file location of the matrix configuration yaml file.
     *
     * @param pathString
     */
    private fun resolvePathIfRelative(pathString: String): String =
        pathString.let {
            if (it.startsWith("$")) {
                Path.of(this.path.parent.pathString, it.replaceFirstChar { "" }).toString()
            } else {
                it
            }
        }

    // Here the individual entries describing related time periods are converted into a list of matrices converted
    @Suppress("CyclomaticComplexMethod", "CognitiveComplexMethod")
    private fun processEntries(
        entries: List<Entry>,
        mode: M,
    ): List<Pair<AbsoluteTime, IndexedDoubleMatrix<I>>> {
        class EntryWrapper<C>(
            val entry: Entry,
            val keyExtract: (Entry) -> C,
        ) : Comparable<EntryWrapper<C>> where C : Comparable<C> {
            fun getC(): C {
                return keyExtract(entry)
            }

            override fun compareTo(other: EntryWrapper<C>): Int {
                return this.getC().compareTo(other.getC())
            }
        }

        val futureEntries = PriorityQueue<EntryWrapper<AbsoluteTime>>()
        val activeEntries = PriorityQueue<EntryWrapper<AbsoluteTime>>()

        entries.forEach { entry ->
            futureEntries.add(EntryWrapper(entry) { it.includedStartTime })
        }

        val matrixList = ArrayList<Pair<AbsoluteTime, IndexedDoubleMatrix<I>>>()

        var currentTime = simulationStartInclusive
        // iterate over the entries to find the one that is active at a given time
        while (currentTime < simulationEndExclusive) {
            // pull new started entries into the activeEntries
            while (futureEntries.isNotEmpty() && futureEntries.peek().entry.includedStartTime == currentTime) {
                activeEntries.add(EntryWrapper(futureEntries.poll().entry) { it.excludedEndTime })
            }

            // remove the ended entries from the activeEntries
            while (activeEntries.isNotEmpty() && activeEntries.peek().entry.excludedEndTime <= currentTime) {
                activeEntries.poll()
            }

            val currentActiveEntry = activeEntries.maxByOrNull { it.entry.level }

            require(currentActiveEntry != null) {
                "${path.fileName} does not specify a matrix for $mode at simulation time $currentTime!"
            }

            val entry = currentActiveEntry.entry
            val path = entry.path
            val parser = entry.parser
            val matrix = matrixMapCache.getOrPut(path to parser) {
                parser.getMatrix(Path.of(path))
            }

            if (matrixList.isEmpty() || matrixList.last().second != matrix) {
                matrixList.add(currentTime to matrix)
            }

            currentTime = if (activeEntries.isNotEmpty()) {
                listOfNotNull(
                    activeEntries.peek()?.getC(),
                    futureEntries.peek()?.getC()
                ).minOrNull() ?: simulationEndExclusive
            } else {
                futureEntries.peek()?.getC() ?: simulationEndExclusive
            }
        }
        return matrixList
    }

    /*
     * The level determines which identifiers can overlay other identifiers. Higher level overrides lower level
     */
    private inner class Entry(
        // inner class to use type parameter I of container class
        val includedStartTime: AbsoluteTime,
        val excludedEndTime: AbsoluteTime,
        val path: String,
        val level: Int,
        val parser: MatrixFormat<I>,
    ) : Comparable<Entry> {
        override fun compareTo(other: Entry): Int {
            return compareValuesBy(this, other, { it.includedStartTime }, { it.level })
        }
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
    fun getDays(): Pair<List<DayOfWeek>, Int> {
        val weekdays = when (this) {
            Monday -> listOf(DayOfWeek.MONDAY)
            Tuesday -> listOf(DayOfWeek.TUESDAY)
            Wednesday -> listOf(DayOfWeek.WEDNESDAY)
            Thursday -> listOf(DayOfWeek.THURSDAY)
            Friday -> listOf(DayOfWeek.FRIDAY)
            Saturday -> listOf(DayOfWeek.SATURDAY)
            Sunday -> listOf(DayOfWeek.SUNDAY)
            Weekday -> listOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
            )

            Everyday -> DayOfWeek.entries
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
