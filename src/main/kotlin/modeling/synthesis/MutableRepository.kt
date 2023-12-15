package modeling.synthesis

import Builder
import Identifiable

interface MutableRepository<E>: Repository<E> {
    override val elements: MutableCollection<E>
    companion object {
        fun <E> from(resource: Resource<E>): MutableRepository<E>
                = LazyRepository(resource.elements)
    }

    fun execute(transformation: (E) -> E?) {
        val mapped = elements.mapNotNull { transformation(it) }
        elements.clear()
        elements.addAll(mapped)
    }

    fun filter(test: (E) -> Boolean) {
        val filtered = elements.filter { test(it) }.toSet()
        elements.retainAll(filtered)
    }
    fun asSequence(): Sequence<E> = elements.asSequence()

}


interface MutableIdRepository<E>: MutableRepository<E>, IdRepository<E> where E: Identifiable<E>{
    companion object {
        fun <E> from(resource: Resource<E>): MutableIdRepository<E> where E: Identifiable<E>
                = LazyIdRepository(resource.elements)
    }
}

fun <R, E> R.close(): IdRepository<E> where R: MutableIdRepository<E>, E: Identifiable<E> = this
fun <R, E> R.close(): Repository<E> where R: MutableRepository<E>, E: Identifiable<E> = this

fun <R, B, E> R.build(): IdRepository<E> where R: MutableIdRepository<B>, B: Builder<E>, E: Identifiable<E> {
    return MapRepository(elements.map { it.build() })
}

fun <R, B, E> R.build(): Repository<E> where R: MutableRepository<B>, B: Builder<E> {
    return ListRepository(elements.map { it.build() })
}




open class LazyRepository<E>(
    private var sequence: Sequence<E>
) : MutableRepository<E> {

    override val elements: MutableCollection<E>
        get() {
            val copy = sequence.toMutableList()
            sequence = copy.asSequence()
            return copy
        }

    override fun execute(transformation: (E) -> E?) {
        sequence = sequence.mapNotNull { transformation(it) }
    }

    override fun filter(test: (E) -> Boolean) {
        sequence = sequence.filter { test(it) }
    }

    override fun asSequence() = sequence

}

class LazyIdRepository<E>(
    entities: Sequence<E>
): LazyRepository<E>(entities), MutableIdRepository<E> where E: Identifiable<E>
