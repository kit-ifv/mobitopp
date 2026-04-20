package application.syntheticsim

import BIELEFELD
import domain.shared.enums.LegacyMode
import domain.shared.enums.Mode
import domain.shared.location.ConstantMetric
import domain.shared.location.CostMetric
import domain.shared.location.DistanceMetric
import domain.shared.location.DurationMetric
import domain.shared.location.Impedance
import domain.shared.location.LegacyZone
import domain.shared.location.LocationMetric
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.shared.location.attributes.HasZoneID
import domain.shared.location.point
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.kilometers
import generateZones
import org.junit.jupiter.api.Test
import utils.collections.cartesianProduct
import utils.units.AbsoluteTime
import utils.units.Time
import utils.units.sinceStart
import java.util.NavigableMap
import java.util.TreeMap
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Manually controllable impedance.
 * [startingModes] controls the standard modes
 */
@Suppress("NotImplementedDeclaration") // For test stubs this is not an issue
class ControllableImpedance(
    private val startingModes: Collection<Mode> = LegacyMode.entries,
    var standardCost: Currency = 1.euros,
    var standardTime: Duration = 10.minutes,
    var standardDistance: Distance = 1.kilometers,
) : Impedance {

    private val currencyMap: MutableMap<Mode, RangeMap<Time, Currency>> =
        mutableMapOf()
    private val durationMap: MutableMap<Mode, RangeMap<Time, Duration>> =
        mutableMapOf()
    private val distanceMap: MutableMap<Mode, MapMetric<Distance>> = mutableMapOf()

    private val defaultCost: LocationMetric<Currency> = ConstantMetric(standardCost)
    private val defaultTime: DurationMetric = ConstantMetric(standardTime)
    private val defaultDistance: DistanceMetric = ConstantMetric(standardDistance)

    /**
     * Either return a saved metric, or the [defaultTime] metric which always returns [standardTime]
     */
    override fun durationMetric(mode: Mode, time: Time): DurationMetric {
        return durationMap[mode]?.get(time) ?: defaultTime
    }

    /**
     * Either return a cost metric found in [currencyMap] or return the [defaultCost] metric, which itself
     * returns the [standardCost] for all requests.
     */
    override fun costMetric(mode: Mode, time: Time): CostMetric {
        return currencyMap[mode]?.get(time) ?: defaultCost
    }

    /**
     * Either return a distance metric found in [distanceMap] or return the [defaultDistance] metric, which itself
     * returns the [standardDistance] for all requests.
     */
    override fun distanceMetric(mode: Mode): DistanceMetric {
        return distanceMap[mode] ?: defaultDistance
    }

    /**
     * Set the travel time for an O-D relation between [origin] and [destination] for a target [mode]
     */
    fun setTime(mode: Mode, origin: LegacyZone, destination: LegacyZone, content: Duration) {
        val metric =
            durationMap.getOrPut(mode) {
                RangeMap(AbsoluteTime.MINUS_INFINITY..<AbsoluteTime.INFINITY, standardTime)
            }
        metric[origin, destination] = content
    }

    /**
     * Set the travel time for an O-D relation between [origin] and [destination] for a target [mode] and a given
     * time range
     */
    fun setTime(mode: Mode, origin: LegacyZone, destination: LegacyZone, range: ClosedRange<Time>, content: Duration) {
        val metric =
            durationMap.getOrPut(mode) {
                RangeMap(AbsoluteTime.MINUS_INFINITY..<AbsoluteTime.INFINITY, standardTime)
            }
        metric[origin, destination, range] = content
    }

    /**
     * Set the travel time for an O-D relation between [origin] and [destination] for all modes in [startingModes]
     */
    fun setTime(origin: LegacyZone, destination: LegacyZone, content: Duration) {
        startingModes.forEach { setTime(it, origin, destination, content) }
    }

    fun setTime(origin: LegacyZone, destination: LegacyZone, range: ClosedRange<Time>, content: Duration) {
        startingModes.forEach { setTime(it, origin, destination, range, content) }
    }

    /**
     * Set the cost for an O-D relation between [origin] and [destination] for a target [mode]
     */
    fun setCost(mode: Mode, origin: LegacyZone, destination: LegacyZone, content: Currency) {
        val metric =
            currencyMap.getOrPut(mode) {
                RangeMap(AbsoluteTime.MINUS_INFINITY..<AbsoluteTime.INFINITY, standardCost)
            }

        metric[origin, destination] = content
    }

    /**
     * Set the cost for an O-D relation between [origin] and [destination] for a target [mode]
     */
    fun setCost(mode: Mode, origin: LegacyZone, destination: LegacyZone, range: ClosedRange<Time>, content: Currency) {
        val metric =
            currencyMap.getOrPut(mode) {
                RangeMap(AbsoluteTime.MINUS_INFINITY..<AbsoluteTime.INFINITY, standardCost)
            }

        metric[origin, destination, range] = content
    }

    /**
     * Set the cost for an O-D relation between [origin] and [destination] for all modes in [startingModes]
     */

    fun setCost(origin: LegacyZone, destination: LegacyZone, content: Currency) {
        startingModes.forEach { setCost(it, origin, destination, content) }
    }

    fun setCost(origin: LegacyZone, destination: LegacyZone, range: ClosedRange<Time>, content: Currency) {
        startingModes.forEach { setCost(it, origin, destination, range, content) }
    }

    /**
     * Set the distance for an O-D relation between [origin] and [destination] for a target [mode]
     */
    fun setDistance(mode: Mode, origin: LegacyZone, destination: LegacyZone, content: Distance) {
        val metric = distanceMap.getOrPut(mode) { MapMetric { standardDistance } }
        metric[origin, destination] = content
    }

    /**
     * Set the distance for an O-D relation between [origin] and [destination] for all modes in [startingModes]
     */

    fun setDistance(origin: LegacyZone, destination: LegacyZone, content: Distance) {
        startingModes.forEach { setDistance(it, origin, destination, content) }
    }

    fun Collection<LegacyZone>.generateRandomValues(
        travelTimes: Pair<Number, Number>,
        travelDistances: Pair<Number, Number>,
        travelCost: Pair<Number, Number>,
        random: Random = Random(1),
    ) {
        val rng: (Pair<Number, Number>) -> Double = {
            random.nextDouble(it.first.toDouble(), it.second.toDouble())
        }

        this.cartesianProduct(this).forEach { (origin, destination) ->
            startingModes.forEach { mode ->

                setDistance(mode, origin, destination, rng(travelDistances).kilometers)
                setCost(mode, origin, destination, rng(travelCost).euros)
                setTime(mode, origin, destination, rng(travelTimes).minutes)
            }
        }
    }
}

class RangeMap<T : Comparable<T>, V>(initialRange: OpenEndRange<T>, initialValue: V) {

    private val values: NavigableMap<T, MapMetric<V>> = TreeMap()

    init {
        this[initialRange] = initialValue
    }

    operator fun set(range: OpenEndRange<T>, value: V) {
        this[range.start, range.endExclusive] = value
    }

    operator fun set(start: T, end: T, value: V) {
        require(start < end) {
            "End must be greater than start"
        }

        val restore = values.floorEntry(end)?.value
        val badKeys = values.keys.filter { it in start..end }.toSet()
        values -= badKeys
        values[start] = MapMetric { value }
        restore?.let { values[end] = it }
    }

    operator fun set(origin: Zone, destination: Zone, value: V) {
        values.values.forEach { it[origin, destination] = value }
    }

    operator fun set(origin: Zone, destination: Zone, range: ClosedRange<T>, value: V) {
        this[range] = value
        val filter = values.filter { it.key in range.start..<range.endInclusive }
        filter.map { it.value[origin, destination] = value }
    }

    operator fun set(range: ClosedRange<T>, value: V) {
        this[range.start, range.endInclusive] = value
    }

    operator fun get(target: T): MapMetric<V> {
        return values.floorEntry(target).value
    }
}

class ControllableImpedanceTest {
    private lateinit var impedance: ControllableImpedance
    private val zone = generateZones(2)
    private val origin = zone[0]

    private val destination = zone[1]

    @BeforeTest
    fun setup() {
        impedance = ControllableImpedance()
    }

    /**
     * I need a test for time controlled impedance to work correctly.
     *
     */
    @Test
    fun testControlledImpedance() {
        impedance.setTime(origin, destination, 0.hours.sinceStart..1.hours.sinceStart, 999.hours)
        impedance.setTime(origin, destination, 0.5.hours.sinceStart..1.5.hours.sinceStart, 888.hours)
        assertEquals(
            impedance.duration(
                origin.point(BIELEFELD),
                destination.point(BIELEFELD),
                LegacyMode.PASSENGER,
                (-1).hours.sinceStart
            ),
            10.minutes
        )
        assertEquals(
            impedance.duration(
                origin.point(BIELEFELD),
                destination.point(BIELEFELD),
                LegacyMode.PASSENGER,
                (0).hours.sinceStart
            ),
            999.hours
        )
        assertEquals(
            impedance.duration(
                origin.point(BIELEFELD),
                destination.point(BIELEFELD),
                LegacyMode.PASSENGER,
                (1).hours.sinceStart
            ),
            888.hours
        )
        assertEquals(
            impedance.duration(
                origin.point(BIELEFELD),
                destination.point(BIELEFELD),
                LegacyMode.PASSENGER,
                (1.49).hours.sinceStart
            ),
            888.hours
        )
        assertEquals(
            impedance.duration(
                origin.point(BIELEFELD),
                destination.point(BIELEFELD),
                LegacyMode.PASSENGER,
                (1.5).hours.sinceStart
            ),
            10.minutes
        )
        assertEquals(
            impedance.duration(
                origin.point(BIELEFELD),
                destination.point(BIELEFELD),
                LegacyMode.PASSENGER,
                (2).hours.sinceStart
            ),
            10.minutes
        )
    }
}

class MapMetric<R>(private val standardValue: () -> R) : LocationMetric<R> {
    private val fields: MutableMap<Pair<ZoneId, ZoneId>, R> = mutableMapOf()

    override fun evaluate(origin: HasZoneID, destination: HasZoneID): R {
        return fields[Pair(origin.zoneID, destination.zoneID)] ?: standardValue()
    }

    operator fun set(origin: Zone, destination: Zone, content: R) {
        fields[Pair(origin.id, destination.id)] = content
    }
}
