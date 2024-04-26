package datastructure

import kotlin.time.Duration.Companion.hours

abstract class ExamplePlan {
    protected val activity1: Activity = Activity.fromDuration(START, 0.hours, 7.hours)
    protected val leg1: Leg = Leg.fromDuration(7.hours, 0.5.hours, START, FOURTH)
    protected val leg1b: Leg = Leg.fromDuration(7.5.hours, 0.5.hours, FOURTH, OTHER)
    protected val activity2: Activity = Activity.fromDuration(OTHER, 8.hours, 4.hours)
    protected val activity2b: Activity = Activity.fromDuration(OTHER, 12.hours, 4.hours)
    protected val leg2: Leg = Leg.fromDuration(16.hours, 0.5.hours, OTHER, FOURTH)
    protected val leg2b: Leg = Leg.fromDuration(16.5.hours, 3.5.hours, FOURTH, THIRD)
    protected val activity3: Activity = Activity.fromDuration(THIRD, 20.hours, 10.hours)
}