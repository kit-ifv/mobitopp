package datastructure

import java.util.*
import kotlin.time.Duration


interface Triip {
    val legs: List<MovingAction>
    val previousAction: StationaryAction?
    val nextAction: StationaryAction?
    fun alternate(lambda: Trip.() -> SortedSet<Leg>)
}


class Trip(internal val legBlock: LegBlock) : Comparable<Trip> {
    val size get() = legBlock.item.size
    val legs: SortedSet<out MovingAction> get() = legBlock.item
    fun overwrite(lambda: EditableTrip.() -> Unit): Boolean {
        val start = legBlock.previous.lastElementOrNull()
        val end = legBlock.next.firstElementOrNull()
        val editableTrip = EditableTrip(start, end, legBlock.item.toList())
        editableTrip.lambda()
        val actions = listOf(start) + editableTrip.legs + end
        if (actions.filterNotNull().isConsistent()) {
            legBlock.replaceAll(legBlock.item, editableTrip.legs)
            return true
        }
        println("Sorry: ${editableTrip.legs} is not consistent. Try again")
        return false
    }

    fun example(lambda: EditableTrip.() -> Unit): Pair<SortedSet<out MovingAction>, SortedSet<out MovingAction>> {
        val start = legBlock.previous.lastElementOrNull()
        val end = legBlock.next.firstElementOrNull()
        val editableTrip = EditableTrip(start, end, legBlock.item.toList())
        editableTrip.lambda()
        val actions = listOf(start) + editableTrip.legs + end
        if (actions.filterNotNull().isConsistent()) {
//            legBlock.replaceAll(legBlock.item, editableTrip.legs)
            return legBlock.item to editableTrip.legs
        }
        println("Sorry: ${editableTrip.legs} is not consistent. Try again")
        return legBlock.item to legBlock.item
    }

    fun matches(target: LegBlock): Boolean {
        return legBlock === target
    }

    inner class EditableTrip(
        val previousEndTime: Duration,
        val previousEndLocation: Location?,
        val nextStartTime: Duration,
        val nextStartLocation: Location?,
        val originals: List<MovingAction>
    ) {
        val legs: SortedSet<Leg> = sortedSetOf()

        // TODO restore assertion that a schedule always has a start activity, and that a trip can only exist between
        // two real existing activity blocks
        constructor(previous: Activity?, next: Activity?, originals: List<MovingAction>) : this(
            previous?.endTime ?: -Duration.INFINITE,
            previous?.endLocation,
            next?.startTime ?: Duration.INFINITE,
            next?.startLocation,
            originals
        )

        operator fun Leg.unaryPlus() {
            legs.add(this)
        }

        operator fun Collection<Leg>.unaryPlus() {
            legs.addAll(this)
        }
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Trip): Int {
        return legBlock.compareTo(other.legBlock)
    }
}

class LinkedTrip(private val original: Trip, private val dispatcher: Dispatcher) : Comparable<LinkedTrip> {
    val size get() = original.size

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: LinkedTrip): Int {
        return original.compareTo(other.original)
    }

    fun matches(legBlock: LegBlock): Boolean {
        return original.matches(legBlock)
    }
    fun overwrite(lambda: Trip.EditableTrip.() -> Unit) {
        val target = original.example(lambda)
        dispatcher.replaceLegs(target.first, target.second)
    }
}