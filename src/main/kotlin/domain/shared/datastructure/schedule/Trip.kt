package domain.shared.datastructure.schedule

import domain.shared.datastructure.schedule.plans.IDispatcher
import domain.shared.datastructure.schedule.replanning.ReplanningStrategy
import domain.shared.enums.MODEUNKOWN
import domain.shared.enums.Mode
import domain.shared.location.LOCATIONUNKNOWN
import domain.shared.location.Location
import domain.shared.location.Metrics
import utils.units.AbsoluteTime
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * A trip consists of a list of [legs] as well as a [previousAction] and a [nextAction]. All references in this class
 * are readonly properties that do not allow alterations of the underlying actions. If
 */
interface Trip {
    // Rename convenience
    val legs: List<Leg>
    val previousAction: StationaryAction?
    val nextAction: StationaryAction?

    val origin get() = previousAction?.location ?: legs.firstOrNull()?.startLocation ?: LOCATIONUNKNOWN
    val destination get() = nextAction?.location ?: legs.lastOrNull()?.endLocation ?: LOCATIONUNKNOWN

    fun alternate(lambda: TripBuilder.() -> Unit)

    fun isConsistent() = (listOf(previousAction) + legs + nextAction).filterNotNull().isConsistent()
}

fun Trip.alternateByImpedance(impedance: Metrics, lambda: ImpedanceBuilder.() -> Unit) {
    alternate {
        byImpedance(impedance, lambda)
    }
}

/**
 * A Default implementation for a trip
 */
class RawTrip(
    override val legs: MutableList<Leg>,
    override val previousAction: Activity?,
    override val nextAction: Activity?
) : Trip {

    override fun alternate(lambda: TripBuilder.() -> Unit) {
        val builder = TripBuilder(this, TODO())
        builder.lambda()
        val target = builder.output()
        legs.clear()
        legs.addAll(target)
    }
}

class ImpedanceBuilder(val impedance: Metrics, private val tripBuilder: TripBuilder) {
    fun taking(modeLocation: Pair<Mode, Location>) {
        tripBuilder.taking(
            modeLocation,
            impedance.duration(
                tripBuilder.currentLocation,
                modeLocation.second,
                modeLocation.first,
                tripBuilder.currentTime
            )
        )
    }
}

/**
 * The trip builder allows the creation of a new trip based on an original trip or otherwise specified input data.
 *
 */
class TripBuilder(
    val previousAction: StationaryAction?,
    val nextAction: StationaryAction?,
    val originals: List<MovingAction>,
    val delayReplanningStrategy: ReplanningStrategy
) {

    constructor(trip: Trip, delayReplanningStrategy: ReplanningStrategy) : this(trip.previousAction, trip.nextAction, trip.legs, delayReplanningStrategy)

    // The previous action could be null, however the assumption that a previous location exists still holds, so I can
    // request the promise that this value will be set eventually.
    lateinit var currentLocation: Location
        private set

    var currentTime: AbsoluteTime = AbsoluteTime.MINUS_INFINITY
        private set

    init {
        if (previousAction != null) {
            currentLocation = previousAction.location
            currentTime = previousAction.endTime
        }
    }

    private val legs = sortedSetOf<Leg>()
    operator fun Leg.unaryPlus() {
        legs.add(this)
    }

    operator fun Collection<Leg>.unaryPlus() {
        legs.addAll(this)
    }

    operator fun Step.unaryPlus() {
        require(duration >= Duration.ZERO) {
            "Negative Duration is not supported. currentTime=$currentTime duration=$duration"
        }

        val posDuration = duration.takeIf { it > Duration.ZERO } ?: 1.seconds

        legs.add(Leg.fromDuration(currentTime, duration = posDuration, currentLocation, location, mode))
        currentTime += posDuration
        currentLocation = location
    }

    fun byImpedance(impedance: Metrics, lambda: ImpedanceBuilder.() -> Unit) {
        ImpedanceBuilder(impedance, this).apply(lambda)
    }

    fun taking(modeLocation: Pair<Mode, Location>, duration: Duration) {
        +Step(modeLocation.second, duration, modeLocation.first)
    }

    operator fun Pause.unaryPlus() {
        currentTime += idleTime
    }

    fun output(): SortedSet<Leg> = legs
    inner class Step(val location: Location, val duration: Duration, val mode: Mode = MODEUNKOWN)
    inner class Pause(val idleTime: Duration)
}

/**
 * A [LinkTrip] is created from a [LinkedTrip] in the [BlockModel] plan. It holds a reference to the model dispatcher,
 * which is set to null if the trip is removed from [BlockModel.legBlockList], so that even when a reference to the
 * link trip object is held someplace else, the changes only propagate into the models if the trip is actually a part
 * of the models.
 */
class LinkTrip constructor(
    private val legBlock: LinkedTrip,
    private var dispatcher: IDispatcher?,
    private val schedule: Schedule? = null,
) : Trip, Comparable<LinkTrip>, Representative<LinkedLeg> {
    override val previousAction: StationaryAction? =
        schedule?.pastActivities()?.last() ?: legBlock.previous.lastElementOrNull()

    override val elements: List<LinkedLeg>
        get() = legBlock.item.toList()

    override fun <X> accept(actionBlockVisitor: ActionBlockVisitor<X>): X {
        return actionBlockVisitor.visitTrip(this)
    }

    override val legs: List<Leg>
        get() = legBlock.item.toList()

    //    override val previousAction: StationaryAction?
//        get() = legBlock.previous.lastElementOrNull()
    override val nextAction: StationaryAction?
        get() = _nextAction
    val previousTrip: List<Leg>?
        get() = legBlock.previous.previous?.item?.toList()
    val nextTrip: List<Leg>?
        get() = legBlock.next.next?.item?.toList()
    private val _nextAction get() = legBlock.next.firstElementOrNull()
    override fun alternate(lambda: TripBuilder.() -> Unit) {
        val builder = TripBuilder(previousAction, nextAction, legs, TODO())
        builder.lambda()
        val newLegs = builder.output()
        // TODO Robin: There should be a better way to force a trip into a block. Also Test this behaviour
        newLegs.lastOrNull()?.let { leg ->
            _nextAction?.let { nextAction ->
                if (nextAction.startTime < leg.endTime) {

                    val projectedEndTime = leg.endTime + nextAction.duration
                    TODO()

                    nextAction.shiftStartTo(leg.endTime)
                }

            }
        }
        dispatcher?.replaceLegs(legs.toSortedSet(), newLegs)
    }

    internal fun removeDispatcher() {
        dispatcher = null
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: LinkTrip): Int {
        return legBlock.compareTo(other.legBlock)
    }

    fun matches(other: LinkedTrip) = legBlock === other
}
