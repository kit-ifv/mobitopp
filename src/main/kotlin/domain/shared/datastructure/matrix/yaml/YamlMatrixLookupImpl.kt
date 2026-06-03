package domain.shared.datastructure.matrix.yaml

import core.datastructure.calendarLookup.CalendarWeekLookup
import core.datastructure.calendarLookup.CalendarWeekLookupBuilder
import core.datastructure.calendarLookup.TimeLookupOperation
import core.datastructure.calendarLookup.WeekLookupOperation
import org.yaml.snakeyaml.Yaml
import utils.WithExpiration
import utils.codes.Decodable
import utils.codes.Encodable
import utils.units.AbsoluteTime
import java.nio.file.Path
import kotlin.io.path.inputStream

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
 * A [YamlMatrixLookup] backed by a YAML configuration file.
 *
 * The YAML file defines, for each transport mode, which matrix file is valid
 * at a given absolute time. This class reads that file and constructs the
 * lookup hierarchy:
 *
 * - **Mode dimension**: the top-level map is keyed by mode (decoded via [modeDecoder]).
 * - **Week dimension**: each mode contains a [core.datastructure.calendarLookup.CalendarWeekLookup] of week lookups.
 * - **Day dimension**: each week contains a [core.datastructure.calendarLookup.WeekLookup] of day lookups.
 * - **Time dimension**: each day contains a [core.datastructure.calendarLookup.DayTimeLookup] giving the corresponding parsing instructions
 * bundlea as [YamlInfo].
 *
 * @param M the type representing modes (decoded from YAML keys via [modeDecoder])
 * @param yamlPath the path to the YAML configuration file
 * @param modeDecoder decodes YAML keys (e.g. `"car"`, `"bike"`) into [M]
 * @param yamlParsingLogic the parsing logic to use for interpreting specifiers.
 *  Defaults to [YamlParsingLogicImpl] via [YamlParsingLogic.default]
 */
class YamlMatrixLookupImpl<M : Encodable>(
    private val yamlPath: Path,
    private val modeDecoder: Decodable<M>,
    private val yamlParsingLogic: YamlParsingLogic = YamlParsingLogic.default(yamlPath),
) : YamlMatrixLookup<M> {
    private val modeLookup: Map<M, CalendarWeekLookup<YamlInfo>> by lazy {
        val yaml = Yaml()

        // Read the YAML file into a Map
        val yamlMap: YamlMap = yaml.load(
            yamlPath.inputStream(),
        )

        yamlMap.entries.associate { (transportType, weekMap) ->
            val mode = modeDecoder.decode(transportType)
            mode to buildCalendarWeeks(weekMap)
        }
    }
    override operator fun get(mode: M, time: AbsoluteTime): WithExpiration<YamlInfo> =
        requireNotNull(modeLookup[mode]) {
            "Key $mode (type: ${mode::class.simpleName}) is missing in matrix config lookup. Available keys:\n" +
                modeLookup.keys.joinToString("\n") { "  - $it (type: ${it::class.simpleName})" }
        }[time]

    private fun buildCalendarWeeks(weekMap: WeekMap): CalendarWeekLookup<YamlInfo> {
        // Create a ruleset as a map, when the corresponding week hits use the value, else use the default value
        // specified in all weeks. If all weeks is missing implode. Also since there is no cyclic repetition we
        // can avoid creating wild structures to handle specifications.
        val calendarWeekLookupBuilder = CalendarWeekLookupBuilder<YamlInfo>()
        weekMap.forEach { (t, u) ->
            val apply = yamlParsingLogic.parseCalendarLookupOperation(t)
            calendarWeekLookupBuilder.apply(parseWeekLookups(u))
        }
        val build = calendarWeekLookupBuilder.build()
        return build
    }

    private fun parseWeekLookups(dayMap: DayMap): Collection<WeekLookupOperation<YamlInfo>> = dayMap.map { (t, u) ->
        val timeOperations = parseTimeLookups(u)
        yamlParsingLogic.parseWeekLookupOperation(t, timeOperations)
    }

    private fun parseTimeLookups(timeMap: TimeMap): Collection<TimeLookupOperation<YamlInfo>> = timeMap.map { (t, u) ->
        val (key, value) = u.entries.first()
        yamlParsingLogic.parseTimeLookupOperation(t, key to value as String)
    }
}
