package domain.shared.datastructure.matrix

import core.datastructure.matrix.DayIdentifier
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


/**
 * Contract for parsing the YAML layout that defines matrix availability in the simulation.
 *
 * A YAML matrix configuration contains hierarchical specifiers:
 * - **Week specifiers** (e.g. `"all weeks"`, `"week 23"`, `"week 12, 13"`)
 * - **Day specifiers** (e.g. `"Monday"`, `"Weekday"`, `"Everyday"`)
 * - **Time specifiers** (e.g. `"08:00 to 12:00"`) with details `(parserName, path)`
 *
 * Parsing such a file requires handling all three specifier types. Accordingly,
 * [YamlParsingLogic] extends [ParseWeekSpecifier], [ParseDaySpecifier], and [ParseTimeSpecifier].
 *
 * Each parser method produces an *operation object*:
 * - [ParseWeekSpecifier] → [CalendarLookupOperation]
 * - [ParseDaySpecifier] → [WeekLookupOperation]
 * - [ParseTimeSpecifier] → [TimeLookupOperation]
 *
 * These operations can then be applied to the builder hierarchy
 * ([CalendarWeekLookupBuilder] → [WeekLookupBuilder] → [DayLookupBuilder]),
 * constructing the full [MatrixLookup] structure from the YAML specification.
 *
 * A default implementation is provided by [YamlParsingLogicImpl], accessible
 * via [YamlParsingLogic.default].
 */
interface YamlParsingLogic : ParseWeekSpecifier<YamlInfo>, ParseDaySpecifier<YamlInfo>,
    ParseTimeSpecifier<YamlInfo> {
    companion object {
        /**
         * Returns the default implementation of [YamlParsingLogic],
         * which uses [YamlParsingLogicImpl] with the given YAML file path.
         */
        fun default(yamlPath: Path): YamlParsingLogic {
            return YamlParsingLogicImpl(yamlPath)
        }
    }
}

/**
 * Default implementation of [YamlParsingLogic].
 *
 * This class performs the actual string parsing and path resolution logic
 * for week, day, and time specifiers in the YAML matrix configuration.
 *
 * Responsibilities delegated from [YamlParsingLogic]:
 * - Splits and normalizes specifier strings.
 * - Resolves relative paths (entries starting with `"$"`) against the
 *   parent directory of the provided YAML file.
 * - Produces the corresponding operation objects
 *   ([CalendarLookupOperation], [WeekLookupOperation], [TimeLookupOperation])
 *   that can be applied to the builder hierarchy.
 *
 * Typically accessed through [YamlParsingLogic.default].
 */
internal class YamlParsingLogicImpl(private val path: Path) : YamlParsingLogic {
    override fun parseWeekSpecifier(string: String): CalendarLookupOperation<YamlInfo> {
        return when (string) {
            "all weeks" -> CalendarLookupOperation {
                applyDefaultInstructions(it)


            }

            else -> CalendarLookupOperation { day ->
                val weeks = string.split(",").map { it.trim().removePrefix("week ").toInt() }
                weeks.forEach {
                    this[it] = day
                }
            }
        }
    }

    override fun parseDaySpecifier(string: String, dayOperations: Collection<TimeLookupOperation<YamlInfo>>): WeekLookupOperation<YamlInfo>{
        val dayIdentifier = DayIdentifier.fromString(string)
        val day = dayIdentifier.getDays().first.first()
        return when (dayIdentifier) {
            DayIdentifier.Weekday ->  WeekLookupOperation{

                dayOperations.forEach { instruction ->
                    setWorkdays(instruction)
                }

            }
            DayIdentifier.Everyday ->  WeekLookupOperation{

                dayOperations.forEach { instruction ->
                    setDefault(instruction)
                }
            }
            else -> WeekLookupOperation{

                dayOperations.forEach { instruction ->
                    this.set(day, instruction)

                }
            }

        }

    }

//    override fun parseDaySpecifier(string: String, builder: DayLookupBuilder<YamlInfo>): WeekLookupOperation<YamlInfo> {
//        val dayIdentifier = DayIdentifier.fromString(string)
//        return when (dayIdentifier) {
//            DayIdentifier.Weekday ->  {a: WeekLookupBuilder<YamlInfo> ->
//                a.setWorkdays(builder)
//                setWorkdays(it)
//            }
//
//            DayIdentifier.Everyday ->  {e : WeekLookupBuilder<YamlInfo>->
//
//                e.setDefault()
//                setDefault(it)
//            }
//
//            else -> WeekLookupOperation {
//                this[dayIdentifier.getDays().first.first()] = it
//            }
//        }
//    }

    override fun parseTimeSpecifier(string: String, details: Pair<String, String>): TimeLookupOperation<YamlInfo> {
        return TimeLookupOperation {
            val (startTime, endTime) = string.split(" to ")
            val (startHour, startMinute) = startTime.split(":").map { it.toInt() }
            val (endHour, endMinute) = endTime.split(":").map { it.toInt() }

            this[startHour.hours + startMinute.minutes, endHour.hours + endMinute.minutes] = YamlInfo(
                details.first,
                resolvePathIfRelative(details.second)
            )


        }
    }

    private fun resolvePathIfRelative(pathString: String): Path =
        pathString.let {
            if (it.startsWith("$")) {
                Path.of(path.parent.pathString, it.replaceFirstChar { "" })
            } else {
                Path.of(it)
            }
        }
}

/**
 * Parses a **week specifier** from a YAML entry and produces a [CalendarLookupOperation].
 *
 * A week specifier string (e.g. `"all weeks"`, `"week 23"`, `"week 12, 13"`) determines
 * which calendar weeks should be affected by subsequent definitions.
 *
 * The result is a [CalendarLookupOperation], which applies day-level definitions to the selected weeks.
 *
 * Interaction with other classes:
 * - Operates at the top level of the builder chain.
 * - Works with [CalendarWeekLookupBuilder] to insert [WeekLookup] definitions.
 */
fun interface ParseWeekSpecifier<T> {
    fun parseWeekSpecifier(string: String): CalendarLookupOperation<T>
}

/**
 * Represents an operation at the **calendar-week level**.
 *
 * A [CalendarLookupOperation] defines how a given input [WeekLookupBuilder] should be handled in the parsing process
 *
 * Interaction:
 * - Operates on [CalendarWeekLookupBuilder].
 * - Inserts or updates week-level definitions with the provided [WeekLookupBuilder].
 */
fun interface CalendarLookupOperation<T> {
    fun CalendarWeekLookupBuilder<T>.apply(instructions: Collection<WeekLookupOperation<T>>)
}

/**
 * Parses a **day specifier** string (e.g. `"Monday"`, `"Weekday"`, `"Everyday"`)
 * and produces a [WeekLookupOperation].
 *
 * The resulting [WeekLookupOperation] applies day-level definitions to one
 * or more days in a [WeekLookupBuilder].
 */
fun interface ParseDaySpecifier<T> {
    fun parseDaySpecifier(string: String, dayOperations: Collection<TimeLookupOperation<T>>): WeekLookupOperation<T>
}

/**
 * Represents an operation at the **week level**.
 *
 * A [WeekLookupOperation] defines how a given input [DayLookupBuilder] should be incorporated in the
 * [WeekLookup]
 * It is typically created by parsing a **day specifier**.
 *
 * Interaction:
 * - Operates on [WeekLookupBuilder].
 * - Configures day schedules using one or more [DayLookupBuilder]s.
 */
fun interface WeekLookupOperation<T> {
    fun WeekLookupBuilder<T>.apply()

}


/**
 * Parses a **time specifier** string (e.g. `"08:00 to 12:00"`) and accompanying details
 * (e.g. `(parser name, path)`), producing a [TimeLookupOperation].
 *
 * The resulting [TimeLookupOperation] applies a time interval to a [DayLookupBuilder].
 */
fun interface ParseTimeSpecifier<T> {
    fun parseTimeSpecifier(string: String, details: Pair<String, String>): TimeLookupOperation<T>
}

/**
 * Represents an operation at the **time level**. This interface operates on Yaml Info fields in particular, As
 * in "parser instruction": "path"
 *
 * A [TimeLookupOperation] applies a single time-interval definition to a [DayLookupBuilder].
 * It is typically created by parsing a **time specifier**.
 *
 * Example:
 * ```
 * "08:00 to 12:00" with details ("Visum Matrix", "dummy.mtx.bz2")
 * ```
 * inserts a segment for the morning with the element `("Visum Matrix", "dummy.mtx.bz2")`.
 */
fun interface TimeLookupOperation<T> {
    fun DayLookupBuilder<T>.apply()
}