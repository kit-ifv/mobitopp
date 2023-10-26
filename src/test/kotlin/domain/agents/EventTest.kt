package domain.agents

import ID
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

internal var idCnt: Long = 0L
internal val rand = Random(42)

interface Person: Agent<MutablePerson> {
    val age: Int
    val children: List<Person>
}

class MutablePerson(

    override var age: Int,
): Person, MutableAgent<MutablePerson> {
    override val id: ID = idCnt++
    override val children: MutableList<Person> = mutableListOf()
    override var nextEvent: Event<MutablePerson>? = null
    override val mutableEntity: MutablePerson
        get() = this

    override fun toString() = "P$id"
}

class Spawn(
    override val time: Time = 0.minutes,
    override val receiver: Person
) : Event<MutablePerson> {
    override val priority = 0
    override var valid = true

    override fun visit(mutableReceiver: MutablePerson) = listOf(
        Age(time + 1.days, receiver).also { println("$receiver is born in year ${time.inWholeDays}.") }
    )
}

class Age(
    override val time: Time,
    override val receiver: Person
) : Event<MutablePerson> {
    override val priority = 1
    override var valid = true

    override fun visit(mutableReceiver: MutablePerson): EventList {
        mutableReceiver.age += 1

        if (rand.nextDouble() > 0.005*receiver.age) {
            return listOf(Multiply(time, receiver))
                    .also { println("$receiver turned ${receiver.age} in year ${time.inWholeDays}.") }
        }

        // death
        println("$receiver died at ${receiver.age} years of age in year ${time.inWholeDays}.")
        return listOf()
    }
}

class Multiply(
    override val time: Time,
    override val receiver: Person
): Event<MutablePerson> {
    override val priority = 1
    override var valid = true

    override fun visit(mutableReceiver: MutablePerson): EventList {
        val next = Age(time + 1.days, receiver)

        if (receiver.age in 19..52) {
            if (rand.nextDouble() > 0.1 + ((receiver.age-19)*0.015 + (receiver.children.size)*0.1) ) {
                val child = MutablePerson(0)
                mutableReceiver.children.add(child)
                println("$receiver gives birth to $child at ${receiver.age} years of age in year ${time.inWholeDays}.")

                return listOf(Spawn(time, child), next)
            }
        }

        return listOf(next)
    }
}


class EventTest {

    @Test
    fun simulateAgingPopulation() {

        val initEvents =
            (0..100).map { MutablePerson(rand.nextInt(0, 50)) }
                         .map { Spawn(receiver = it) }

        Simulator(initEvents).run(period = 100.days)


    }
}