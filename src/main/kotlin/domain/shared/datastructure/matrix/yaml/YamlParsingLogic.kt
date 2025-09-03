package domain.shared.datastructure.matrix.yaml

import core.datastructure.calendarLookup.CalendarLookupOperation
import core.datastructure.calendarLookup.TimeLookupBuilder
import core.datastructure.calendarLookup.TimeLookupOperation
import core.datastructure.calendarLookup.WeekLookupBuilder
import core.datastructure.calendarLookup.WeekLookupOperation
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
 * ([core.datastructure.calendarLookup.CalendarWeekLookupBuilder] → [core.datastructure.calendarLookup.WeekLookupBuilder] → [core.datastructure.calendarLookup.TimeLookupBuilder]),
 * layering definitions with priority-based overrides.
 *
 * A default implementation is provided by [YamlParsingLogicImpl], accessible
 * via [YamlParsingLogic.default].
 */
interface YamlParsingLogic :
    ParseWeekSpecifier<YamlInfo>,
    ParseDaySpecifier<YamlInfo>,
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
    override fun parseCalendarLookupOperation(string: String): CalendarLookupOperation<YamlInfo> {
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

    override fun parseWeekLookupOperation(
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

    override fun parseTimeLookupOperation(
        string: String,
        details: Pair<String, String>,
    ): TimeLookupOperation<YamlInfo> {
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
    fun parseCalendarLookupOperation(string: String): CalendarLookupOperation<T>
}

/**
 * Parses a **day specifier** string (e.g. `"Monday"`, `"Weekday"`, `"Everyday"`)
 * and produces a [WeekLookupOperation].
 *
 * A day specifier determines which days of the week should receive
 * the provided [TimeLookupOperation]s.
 */
fun interface ParseDaySpecifier<T> {
    fun parseWeekLookupOperation(
        string: String,
        dayOperations: Collection<TimeLookupOperation<T>>,
    ): WeekLookupOperation<T>
}

/**
 * Parses a **time specifier** string (e.g. `"08:00 to 12:00"`) and details `(parserName, path)`,
 * producing a [TimeLookupOperation].
 *
 * The resulting [TimeLookupOperation] applies a time interval with an element of type [T]
 * to a [TimeLookupBuilder], respecting the given priority.
 */
fun interface ParseTimeSpecifier<T> {
    fun parseTimeLookupOperation(string: String, details: Pair<String, String>): TimeLookupOperation<T>
}
