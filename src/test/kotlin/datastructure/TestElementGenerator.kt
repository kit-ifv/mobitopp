package datastructure

import java.util.*

class SetListBuilder {
    val elements = mutableListOf<DualSetContainer<A, B, C>>()

    fun container(lambda: ContainerBuilder.() -> Unit) {
        val b = ContainerBuilder()
        b.apply(lambda)
        elements.add(b.generate())
    }
}

class ContainerBuilder {
    val f = First()
    val s = Second()

    class First {
        var e: IntArray = IntArray(0)
    }

    class Second {
        var e: IntArray = IntArray(0)
    }

    operator fun First.get(vararg int: Int) {
        e = int
    }

    operator fun Second.get(vararg int: Int) {
        e = int
    }

    fun generate(): DualSetContainer<A, B, C> {
        return DualSetContainer(left = generateLeft(f.e), right = generateRight(s.e))
    }

    private fun generateLeft(int: IntArray): NavigableSet<B> {
        return TreeSet(int.map { B(it) })
    }

    private fun generateRight(int: IntArray): NavigableSet<C> {
        return TreeSet(int.map { C(it) })
    }
}


class ScheduleBuilder {
    val schedule = Schedule()



    operator fun Activity.unaryPlus() {
        schedule.add(this)
    }

    operator fun Leg.unaryPlus() {
        schedule.add(this)
    }

}

