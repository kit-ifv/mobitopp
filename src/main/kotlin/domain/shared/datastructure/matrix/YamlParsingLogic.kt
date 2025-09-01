package domain.shared.datastructure.matrix

import core.datastructure.matrix.DayIdentifier
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


/**
 * Contract for parsing the YAML layout that defines matrix availability.
 *
 * A YAML matrix configuration is hierarchical:
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
 * These operations can then be applied in sequence to the builder hierarchy
 * ([CalendarWeekLookupBuilder] → [WeekLookupBuilder] → [DayLookupBuilder]),
 * layering definitions with priority-based overrides.
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
 * This class provides the concrete string parsing and path resolution logic
 * for week, day, and time specifiers in the YAML matrix configuration.
 *
 * Responsibilities:
 * - Parse week specifiers into [CalendarLookupOperation]s.
 * - Parse day specifiers into [WeekLookupOperation]s, bundling a collection of
 *   [TimeLookupOperation]s.
 * - Parse time specifiers into [TimeLookupOperation]s with explicit priorities.
 * - Resolve relative paths (entries starting with `"$"`) against the parent
 *   directory of the given YAML file.
 *
 * Normally accessed through [YamlParsingLogic.default].
 */
internal class YamlParsingLogicImpl(private val path: Path) : YamlParsingLogic {
    override fun parseWeekSpecifier(string: String): CalendarLookupOperation<YamlInfo> {
        val setAsDefaultFunction: CalendarLookupOperation<YamlInfo> = {
            applyDefaultInstructions(it)
        }
        val setForSpecificWeeks: CalendarLookupOperation<YamlInfo> = { day ->
            val weeks = string.split(",").map { it.trim().removePrefix("week ").toInt() }
            weeks.forEach {
                this[it] = day
            }

        }
        return when (string) {
            "all weeks" -> setAsDefaultFunction
            else -> setForSpecificWeeks
        }
    }

    override fun parseDaySpecifier(
        string: String,
        dayOperations: Collection<TimeLookupOperation<YamlInfo>>,
    ): WeekLookupOperation<YamlInfo> {
        val dayIdentifier = DayIdentifier.fromString(string)
        val day = dayIdentifier.getDays().first.first()

        return when (dayIdentifier) {
            DayIdentifier.Weekday -> aggregateOperations(dayOperations, WeekLookupBuilder<YamlInfo>::setWorkdays)
            DayIdentifier.Everyday -> aggregateOperations(dayOperations, WeekLookupBuilder<YamlInfo>::setDefault)
            else -> aggregateOperations(dayOperations) { this[day] = it }


        }


    }


    private fun aggregateOperations(
        dayOperations: Collection<TimeLookupOperation<YamlInfo>>,
        action: WeekLookupBuilder<YamlInfo>.(TimeLookupOperation<YamlInfo>) -> Unit,
    ): WeekLookupOperation<YamlInfo> = { dayOperations.forEach { action(it) } }

    override fun parseTimeSpecifier(string: String, details: Pair<String, String>): TimeLookupOperation<YamlInfo> {
        return { priority ->
            val (startTime, endTime) = string.split(" to ")
            val (startHour, startMinute) = startTime.split(":").map { it.toInt() }
            val (endHour, endMinute) = endTime.split(":").map { it.toInt() }
            this[startHour.hours + startMinute.minutes, endHour.hours + endMinute.minutes] = YamlInfo(
                details.first,
                resolvePathIfRelative(details.second)
            ) to priority


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
 * Parses a **week specifier** string from a YAML entry and produces a [CalendarLookupOperation].
 *
 * A week specifier string (e.g. `"all weeks"`, `"week 23"`, `"week 12, 13"`)
 * determines which calendar weeks should be targeted.
 *
 * The result is a [CalendarLookupOperation], which applies a collection of
 * [WeekLookupOperation]s to the selected weeks.
 */
fun interface ParseWeekSpecifier<T> {
    fun parseWeekSpecifier(string: String): CalendarLookupOperation<T>
}

/**
 * Represents an operation at the **calendar-week level**.
 *
 * A [CalendarLookupOperation] describes how to apply a set of [WeekLookupOperation]s
 * to a [CalendarWeekLookupBuilder].
 *
 * Typical usage: produced by parsing a week specifier, then applied to a
 * [CalendarWeekLookupBuilder] during YAML processing.
 */
fun interface CalerndarLookupOperation<T> {
    fun CalendarWeekLookupBuilder<T>.apply(instructions: Collection<WeekLookupOperation<T>>)
}
typealias CalendarLookupOperation<T> = CalendarWeekLookupBuilder<T>.(Collection<WeekLookupOperation<T>>) -> Unit

/**
 * Parses a **day specifier** string (e.g. `"Monday"`, `"Weekday"`, `"Everyday"`)
 * and produces a [WeekLookupOperation].
 *
 * A day specifier determines which days of the week should receive
 * the provided [TimeLookupOperation]s.
 */
fun interface ParseDaySpecifier<T> {
    fun parseDaySpecifier(string: String, dayOperations: Collection<TimeLookupOperation<T>>): WeekLookupOperation<T>
}

/**
 * Represents an operation at the **week level**.
 *
 * A [WeekLookupOperation] defines how to insert one or more [TimeLookupOperation]s
 * into a [WeekLookupBuilder].
 *
 * Typically produced by parsing a day specifier.
 */
fun interface WeekLookupOsperation<T> {
    fun apply(to: WeekLookupBuilder<T>)

}
typealias WeekLookupOperation<T> = WeekLookupBuilder<T>.() -> Unit
typealias TimeLookupOperation<T> = DayLookupBuilder<T>.(Int) -> Unit

/**
 * Parses a **time specifier** string (e.g. `"08:00 to 12:00"`) and details `(parserName, path)`,
 * producing a [TimeLookupOperation].
 *
 * The resulting [TimeLookupOperation] applies a time interval with an element of type [T]
 * to a [DayLookupBuilder], respecting the given priority.
 */
fun interface ParseTimeSpecifier<T> {
    fun parseTimeSpecifier(string: String, details: Pair<String, String>): TimeLookupOperation<T>
}

/**
 * Represents an operation at the **time level**.
 *
 * A [TimeLookupOperation] applies a single time-interval definition
 * to a [DayLookupBuilder], together with a priority.
 *
 * Priority determines how overlapping intervals are resolved: higher-priority
 * definitions override lower-priority ones, but lower-priority definitions
 * still fill uncovered gaps.
 *
 * Example:
 * ```
 * "08:00 to 12:00" with details ("Visum Matrix", "dummy.mtx.bz2")
 * ```
 * inserts a segment from 08:00–12:00 with the element `("Visum Matrix", "dummy.mtx.bz2")`
 * at the given priority.
 */
fun interface AAAAA<T> {
    fun applyTo(dayBuilder: DayLookupBuilder<T>, priority: Int)
}

data class PrioritizedOperation<T>(
    val operation: TimeLookupOperation<T>,
    val priority: Int,
)