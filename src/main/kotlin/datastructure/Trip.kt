package datastructure

import datastructure.plans.Dispatcher
import java.util.*
import kotlin.time.Duration

/**
 * A trip consists of a list of [legs] as well as a [previousAction] and a [nextAction]. All references in this class
 * are readonly properties that do not allow alterations of the underlying actions. If
 */
interface Trip {
    val legs: List<MovingAction>
    val size get() = legs.size
    val previousAction: StationaryAction?
    val nextAction: StationaryAction?
    fun alternate(lambda: TripBuilder.() -> Unit)

    fun isConsistent() = (listOf(previousAction) + legs + nextAction).filterNotNull().isConsistent()
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
        val builder = TripBuilder(this)
        builder.lambda()
        val target = builder.output()
        legs.clear()
        legs.addAll(target)
    }
}

/**
 * The trip builder allows the creation of a new trip based on an original trip or otherwise specified input data.
 *
 */
class TripBuilder(
    private val previousAction: StationaryAction?,
    val nextAction: StationaryAction?,
    val originals: List<MovingAction>
) {

    constructor(trip: Trip) : this(trip.previousAction, trip.nextAction, trip.legs)

    // The previous action could be null, however the assumption that a previous location exists still holds, so I can
    // request the promise that this value will be set eventually.
    private lateinit var currentLocation: Location
    private var currentTime: Duration = -Duration.INFINITE

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
        require(duration > Duration.ZERO) {
            "Negative Duration is not supported. currentTime=$currentTime duration=$duration"
        }

        legs.add(Leg.fromDuration(currentTime, duration = duration, currentLocation, location))
        currentTime += duration
        currentLocation = location
    }

    operator fun Pause.unaryPlus() {
        currentTime += idleTime
    }

    fun output(): SortedSet<Leg> = legs
    inner class Step(val location: Location, val duration: Duration)
    inner class Pause(val idleTime: Duration)
}

/**
 * A [LinkTrip] is created from a [LegBlock] in the [BlockModel] plan. It holds a reference to the model dispatcher,
 * which is set to null if the trip is removed from [BlockModel.legBlockList], so that even when a reference to the
 * link trip object is held someplace else, the changes only propagate into the models if the trip is actually a part
 * of the models.
 */
class LinkTrip(private val legBlock: LegBlock, private var dispatcher: Dispatcher?) : Trip, Comparable<LinkTrip> {

    override val legs: List<Leg>
        get() = legBlock.item.toList()
    override val previousAction: StationaryAction?
        get() = legBlock.previous.lastElementOrNull()
    override val nextAction: StationaryAction?
        get() = legBlock.next.firstElementOrNull()

    override fun alternate(lambda: TripBuilder.() -> Unit) {
        val builder = TripBuilder(previousAction, nextAction, legs)
        builder.lambda()
        val target = builder.output()
        dispatcher?.replaceLegs(legs.toSortedSet(), target)
    }

    fun removeDispatcher() {
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

    fun matches(other: LegBlock) = legBlock === other
}
