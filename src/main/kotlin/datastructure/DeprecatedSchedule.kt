package datastructure

interface Location



//typealias Plan = DualSetList<LinkedElement<Action>, LinkedLeg, LinkedActivity>
//
//interface LinkedElement<out T : Action> : Action, Comparable<Action> {
//    val plan: DeprecatedSchedule
//    val original: T
//
//    fun next(): LinkedElement<Action>? {
//        return plan.elements().higher(this)
//    }
//
//    fun previous(): LinkedElement<Action>? = plan.elements().lower(this)
//    fun changeEndLocation(to: Location)
//    fun changeStartLocation(to: Location)
//
//    fun remove()
//
//    override fun compareTo(other: Action): Int {
//        return original.compareTo(other)
//    }
//
//    fun consistentTo(
//        new: Activity,
//        spawner: (startTime: Duration, duration: Duration, start: Location, end: Location) -> Leg
//    ) {
//        if (endLocation != new.startLocation) {
//            plan.add(spawner(endTime, new.startTime - endTime, endLocation, new.startLocation))
//        }
//    }
//
//    fun consistentFrom(
//        new: Action,
//        spawner: (startTime: Duration, duration: Duration, start: Location, end: Location) -> Leg
//    ) {
//        if (endLocation != new.startLocation) {
//            plan.add(spawner(endTime, new.startTime - endTime, endLocation, new.startLocation))
//        }
//    }
//}
//
//class LinkedActivity(
//    override val original: Activity,
//    override val plan: DeprecatedSchedule
//) : Activity, LinkedElement<Activity> {
//
//    override fun changeEndLocation(to: Location) {
//        if (to != original.location) {
//            original.location = to
//            next()?.changeStartLocation(to)
//        }
//
//    }
//
//    override fun changeStartLocation(to: Location) {
//        if (to != original.location) {
//            original.location = to
//            next()?.changeStartLocation(to)
//        }
//
//    }
//
//    override fun remove() {
//        plan.plan.removeRight(this)
//    }
//
//    override var location: Location
//        get() = original.location
//        set(value) {
//            if (value != original.location) {
//                original.location = value
//            }
//        }
//    override var startTime: Duration
//        get() = original.startTime
//        set(value) {}
//    override var endTime: Duration
//        get() = original.endTime
//        set(value) {}
//
//    override fun equals(other: Any?): Boolean {
//        //TODO is it really equality, it could be a different linked plan, taking place at the same time, location etc.
//        if (other !is Activity) return false
//        return other == original
//    }
//
//    override val duration: Duration
//        get() = original.endTime
//
//    override fun toString(): String {
//        return original.toString()
//    }
//
//}
//
//
//
//
//class LinkedLeg(override val original: Leg, override val plan: DeprecatedSchedule) : Leg, LinkedElement<Leg> {
//    override fun changeEndLocation(to: Location) {
//        original.endLocation = to
//        next()?.changeStartLocation(to)
//    }
//
//    override fun changeStartLocation(to: Location) {
//        original.startLocation = to
//        previous()?.changeEndLocation(to)
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (other !is Leg) return false
//        return other == original
//    }
//
//    override var startTime: Duration
//        get() = original.startTime
//        set(value) {
//            if (previous()?.let { it.endTime <= value } != false) {
//                original.startTime = value
//            }
//        }
//    override var endTime: Duration
//        get() = original.endTime
//        set(value) {
//            if (next()?.let { it.startTime <= original.startTime + value } != false) {
//                original.endTime = value
//            }
//        }
//    override var startLocation: Location
//        get() = original.startLocation
//        set(value) {
//            if (value != original.startLocation) {
//                changeStartLocation(value)
//            }
//        }
//    override var endLocation: Location
//        get() = original.endLocation
//        set(value) {
//            if (value != original.endLocation) {
//                changeEndLocation(value)
//            }
//        }
//    override val duration: Duration
//        get() = original.duration
//
//    override fun remove() {
//        plan.plan.removeLeft(this)
//    }
//
//    override fun toString(): String {
//        return original.toString()
//    }
//}

//class DeprecatedSchedule(var current: Duration = Duration.ZERO) {
//    val plan = Plan()
//    val size get() = plan.persistentSet.size
//    fun block(index: Int): DualSetContainer<LinkedElement<Action>, LinkedLeg, LinkedActivity> {
//        return plan.entries[index]
//    }
//
//    fun elements() = plan.persistentSet
//
//    fun addConsistent(activity: Activity) {
//
//        val linkedActivity = LinkedActivity(activity, this)
//        linkedActivity.previous()?.consistentTo(linkedActivity, Leg::fromDuration)
//        linkedActivity.next()?.consistentFrom(linkedActivity, Leg::fromDuration)
//        plan.addRight(linkedActivity)
//    }
//
//    fun add(activity: Activity) {
//        plan.addRight(LinkedActivity(activity, this))
//    }
//
//    fun add(leg: Leg) {
//        plan.addLeft(LinkedLeg(leg, this))
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (other !is DeprecatedSchedule) return false
//        return plan == other.plan
//    }
//}
//
//
//class TripView(private val schedule: DeprecatedSchedule) :
//    Listener<List<DualSetContainer<LinkedElement<Action>, LinkedLeg, LinkedActivity>>> {
//    var trips: List<DeprecatedTrip> = emptyList()
//
//    init {
//
//        schedule.plan.entries.listener.add(this)
//        update(schedule.plan.entries)
//    }
//
//
//    operator fun get(index: Int): DeprecatedTrip {
//        return trips[index]
//    }
//
//    val size get() = trips.size
//    override fun update(new: List<DualSetContainer<LinkedElement<Action>, LinkedLeg, LinkedActivity>>) {
//        val completeTrips = new.zipWithNext { previous, current ->
//            DeprecatedTrip(
//                current.left,
//                previous.right.last(),
//                current.right.first(),
//                schedule
//            )
//        }
//        trips = if (new.first().left.isEmpty()) completeTrips else listOf(
//            DeprecatedTrip(
//                new.first().left,
//                null,
//                new.first().right.first(),
//                schedule
//            )
//        ) + completeTrips
//    }
//
//    override fun toString(): String {
//        return trips.toString()
//    }
//}
//
//
//class DeprecatedTrip(
//    val legs: SortedSet<LinkedLeg>,
//    val previousActivity: LinkedActivity?,
//    val nextActivity: LinkedActivity?,
//    val plan: DeprecatedSchedule
//) {
//
//
//    val startLocation get() = previousActivity?.endLocation ?: legs.first().startLocation
//    val endLocation get() = nextActivity?.startLocation ?: legs.last().endLocation
//
//
//    val duration get() = legs.last().endTime - legs.first().startTime
//    fun forceChange(to: SortedSet<Leg>) {
//        //TODO this method should verify consistency of "to"
//        if (to.isEmpty()) {
//            legs.forEach {
//                plan.plan.removeLeft(it)
//            }
//        } else {
//            val newSet = TreeSet(to.map { LinkedLeg(it, plan) })
////            plan.replaceAll()
////            plan.
////            legs.clear()
////            legs.addAll(newSet)
//
//        }
//
//
//    }
//    fun isConsistent() = legs.isConsistent()
//    fun adapt(lambda: TripBuilder.() -> Unit) {
//        val builder = TripBuilder(this)
//        builder.apply(lambda)
//        val target = builder.build()
//        require(target.isNotEmpty()) {
//            "Cannot work with an empty list of legs for a trip."
//        }
//        val fitsStartLocation = previousActivity?.location == target.first().startLocation
//        val fitsEndLocation = nextActivity?.location == target.last().endLocation
//        val fitsStartTime = previousActivity?.endTime?.let { it <= target.first().startTime } ?: true
//        val fitsEndTime = nextActivity?.startTime?.let { it >= target.last().startTime } ?: true
//        if(fitsStartLocation && fitsEndLocation && fitsStartTime && fitsEndTime) {
//            forceChange(target)
//        } else{
//            println("Could not change trip as it does not fit within the plan")
//        }
//    }
//
//    fun forceDelete() {
//        forceChange(sortedSetOf())
//    }
//
//    fun isEmpty() = legs.isEmpty()
//
//
//    override fun toString(): String {
//        return legs.toString()
//    }
//}
//class TripBuilder(val trip: DeprecatedTrip) {
//    private val legs = sortedSetOf<Leg>()
//    private var currentTime = trip.previousActivity?.endTime ?: Duration.ZERO
//    private var currentLocation = trip.startLocation
//
//    val previousLocation = trip.startLocation
//
//    fun addPause(duration: Duration) {
//        currentTime += duration
//    }
//    fun addLeg(target: Location, duration: Duration) {
//        legs.add(Leg.fromDuration(startTime = currentTime, duration=duration, currentLocation, endLocation = target))
//        currentLocation = target
//        currentTime += duration
//    }
//    fun build() = legs
//
//}
