package synthesis

import ID
import Identifiable
import IdentifiableBuilder


internal typealias IdBuilder<E> = IdentifiableBuilder<E>

interface Resource<E> {
    val elements: Sequence<E>

    fun asRepository(): Repository<E> = Repository.from(this)

    fun asMutableRepository(): MutableRepository<E> = MutableRepository.from(this)
}

interface IdResource<E> : Resource<E> where E: Identifiable<E> {
    override fun asRepository(): IdRepository<E> = IdRepository.from(this)

    override fun asMutableRepository(): MutableIdRepository<E> = MutableIdRepository.from(this)
}

fun <S, E> S.asResource(): Resource<E> where S: Sequence<E> {
    return object:Resource<E> {
        override val elements: Sequence<E>
            get() = this@asResource
    }
}


/**
 * A repository stores and provides access to elements.
 *
 * @param E the generic type of the stored elements
 * @constructor Create empty Repository
 */
interface Repository<E> {
    val elements : Collection<E>
    companion object {
        /**
         * Creates a repository from the given [Resource].
         *
         * @param resource a resource that provides / generates the elements.
         * @param E the generic type of the stored elements.
         * @return a [ListRepository] containing the elements provided by the given [Resource]
         */
        fun <E> from(resource: Resource<E>): Repository<E>
            = ListRepository(resource.elements.toList())

    }

}

/**
 * An [IdRepository] is a [Repository] for [Identifiable] elements.
 * It additionally provides the means to obtain elements by their [ID].
 *
 * @param E the generic type of the [Identifiable] elements to be stored.
 * @constructor Create empty repository for identifiable elements
 */
interface IdRepository<E>: Repository<E> where E: Identifiable<E> {

    companion object {
        /**
         * Creates a repository from the given [Resource].
         *
         * @param resource a resource that provides / generates the elements.
         * @param E the generic type of the stored elements.
         * @return a [MapRepository] containing the elements provided by the given [Resource]
         */
        fun <E> from(resource: Resource<E>): IdRepository<E> where E: Identifiable<E>
                = MapRepository(resource.elements.toList())
    }

    /**
     * @param id the id of the requested element
     * @return an element ith the given id or nul if it does not exist in the repository.
     */
    fun getById(id: ID<E>): E? {
        println("Use list find first")
        return elements.find { id == it.id } // use equals in case type of ID changes
    }
}

/**
 * [ListRepository] is a list-based [Repository] implementation.
 *
 * @param E the generic type of the stored elements
 * @constructor Create empty list repository
 * @property elements the elements to be stored
 */
class ListRepository<E> (
    override val elements: List<E>
): Repository<E>

/**
 * [MapRepository] is a map-based [IdRepository] implementation.
 *
 * @param E the type of the stored elements
 * @param elements the [Identifiable] elements to e stored
 * @constructor Create empty map repository
 */
class MapRepository<E>(
    elements: List<E>
): IdRepository<E> where E: Identifiable<E> {
    override val elements: Collection<E>
        get() = idMap.values

    private val idMap: Map<ID<E>, E> = elements.associateBy { it.id }

    override fun getById(id: ID<E>): E? = idMap[id]
}
