
import datastructure.Location
import datastructure.NewSchedule
import datastructure.OTHER
import datastructure.RawActivity
import datastructure.RawLeg
import datastructure.START
import datastructure.THIRD
import kotlin.time.Duration.Companion.hours

class Person {
    var location: Location = START
    val plan = generatePlan()

    fun nextStep() {
        plan.doNext()
    }
}

private fun generatePlan(): NewSchedule {
    val s = NewSchedule(RawActivity(START, 0.hours, 7.hours))

        s.add(RawLeg( 7.hours, 0.5.hours, START, OTHER))
        s.add(RawActivity(OTHER, 8.hours, 8.hours))
        s.add(RawLeg(16.hours, 0.5.hours, OTHER, THIRD))
        s.add(RawActivity(THIRD, 17.hours, 0.5.hours))
        s.add(RawLeg(20.hours, 0.5.hours, THIRD, START))
        s.add(RawActivity(START, 21.hours, 10.hours))

    return s
}

fun main() {
    val person = Person()
    person.nextStep()

}