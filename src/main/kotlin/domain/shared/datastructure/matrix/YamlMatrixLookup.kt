package domain.shared.datastructure.matrix

import core.datastructure.matrix.DayMap
import core.datastructure.matrix.TimeMap
import core.datastructure.matrix.WeekMap
import core.datastructure.matrix.YamlMap
import domain.shared.datastructure.matrix.YamlParsingLogic
import org.yaml.snakeyaml.Yaml
import utils.Decodable
import utils.Encodable
import utils.WithExpiration
import utils.units.AbsoluteTime
import java.nio.file.Path
import kotlin.io.path.inputStream
/**
 * A [MatrixLookup] backed by a YAML configuration file.
 *
 * The YAML file defines, for each transport mode, which matrix file is valid
 * at a given absolute time. This class reads that file and constructs the
 * lookup hierarchy:
 *
 * - **Mode dimension**: the top-level map is keyed by mode (decoded via [modeDecoder]).
 * - **Week dimension**: each mode contains a [CalendarWeekLookup] of week lookups.
 * - **Day dimension**: each week contains a [WeekLookup] of day lookups.
 * - **Time dimension**: each day contains a [DayTimeLookup] giving the corresponding parsing instructions
 * bundlea as [YamlInfo].
 *
 * @param M the type representing modes (decoded from YAML keys via [modeDecoder])
 * @param yamlPath the path to the YAML configuration file
 * @param modeDecoder decodes YAML keys (e.g. `"car"`, `"bike"`) into [M]
 * @param yamlParsingLogic the parsing logic to use for interpreting specifiers.
 *  Defaults to [YamlParsingLogicImpl] via [YamlParsingLogic.default]
 */
class YamlMatrixLookup<M : Encodable>(
    private val yamlPath: Path,
    private val modeDecoder: Decodable<M>,
    private val yamlParsingLogic: YamlParsingLogic = YamlParsingLogic.default(yamlPath),
) : MatrixLookup<M> {
    private val modeLookup: Map<M, CalendarWeekLookup<YamlInfo>>

    init {
        // Create a YAML instance
        val yaml = Yaml()

        // Read the YAML file into a Map
        val yamlMap: YamlMap = yaml.load(
            yamlPath.inputStream()
        )

        modeLookup = yamlMap.entries.associate { (transportType, weekMap) ->
            val mode = modeDecoder.decode(transportType)
            mode to buildCalendarWeeks(weekMap)
        }


    }

    override operator fun get(mode: M, time: AbsoluteTime): WithExpiration<YamlInfo> {
        return modeLookup.getValue(mode)[time]
    }

    private fun buildCalendarWeeks(weekMap: WeekMap): CalendarWeekLookup<YamlInfo> {
        // Create a ruleset as a map, when the corresponding week hits use the value, else use the default value
        // specified in all weeks. If all weeks is missing implode. Also since there is no cyclic repetition we
        // can avoid creating wild structures to handle specifications.
        val calendarWeekLookupBuilder = CalendarWeekLookupBuilder<YamlInfo>()
        weekMap.forEach { (t, u) ->
            val function = yamlParsingLogic.parseWeekSpecifier(t)
            function.run {
                calendarWeekLookupBuilder.apply(buildWeek(u))
            }
        }
        val build = calendarWeekLookupBuilder.build()
        return build
    }

    private fun buildWeek(dayMap: DayMap): WeekLookup<YamlInfo> {
        val dayLookup = WeekLookupBuilder<YamlInfo>()
        dayMap.forEach { (t, u) ->
            val function = yamlParsingLogic.parseDaySpecifier(t)
            function.run {
                dayLookup.apply(buildDay(u))
            }
        }
        return dayLookup.build()
    }

    private fun buildDay(timeMap: TimeMap): DayLookupBuilder<YamlInfo> {
        val timeLookup = DayLookupBuilder<YamlInfo>()
        timeMap.forEach { (t, u) ->
            val (key, value) = u.entries.first()
            val function = yamlParsingLogic.parseTimeSpecifier(t, key to value as String)

            function.run {
                timeLookup.apply()
            }
        }

        return timeLookup
    }


}