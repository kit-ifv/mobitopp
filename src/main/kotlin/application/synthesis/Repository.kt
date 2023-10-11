package application.synthesis

import ID
import Identifiable
import IdentifiableBuilder


internal typealias IdBuilder<E> = IdentifiableBuilder<E>

internal typealias Resource<E> = () -> Sequence<E>
internal typealias Transformation<E> = (E) -> E?


interface Repository<E> {
    companion object {
        fun <E> from(resource: Resource<E>): Repository<E>
            = SimpleRepository(resource().toList())

    }

    fun getAll(): Collection<E>

}
interface IdRepository<E>: Repository<E> where E: Identifiable {

    companion object {
        fun <E> from(resource: Resource<E>): IdRepository<E> where E: Identifiable
                = MapRepository(resource().toList())
    }
    fun getById(id: ID): E? {
        println("Use list find first")
        return getAll().find { id.equals(it.id()) } // use equals in case type of ID changes
    }
}

class SimpleRepository<E> (
    private val entities: List<E>
): Repository<E> {
    override fun getAll() = entities
}

class MapRepository<E>(
    entities: List<E>
): IdRepository<E> where E: Identifiable {

    private val idMap: Map<ID, E> = entities.associateBy { it.id() }

    override fun getAll(): Collection<E> = idMap.values

    override fun getById(id: ID): E? = idMap[id]
}
