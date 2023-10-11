package application.synthesis

import Builder
import ID
import Identifiable


interface MutableRepository<E>: Repository<E> {

    companion object {
        fun <E> from(resource: Resource<E>): MutableRepository<E>
                = LazyRepository(resource())
    }

    fun apply(transformation: Transformation<E>)

}

interface MutableIdRepository<E>: MutableRepository<E>, IdRepository<E> where E: Identifiable {
    companion object {
        fun <E> from(resource: Resource<E>): MutableIdRepository<E> where E: Identifiable
                = LazyIdRepository(resource())
    }
}

//TODO finish for non builders
fun <R, B, E> R.finish(): IdRepository<E> where R: MutableIdRepository<B>, B: Builder<E>, E: Identifiable {
    return MapRepository(getAll().map { it.build() })
}

fun <R, B, E> R.finish(): Repository<E> where R: MutableRepository<B>, B: Builder<E> {
    return SimpleRepository(getAll().map { it.build() })
}


open class LazyRepository<E>(
    private var entities: Sequence<E>
) : MutableRepository<E> {
    override fun apply(transformation: Transformation<E>) {
        entities = entities.map { transformation(it) }.filterNotNull()
    }

    override fun getAll() = entities.toList()

}

class LazyIdRepository<E>(
    entities: Sequence<E>
): LazyRepository<E>(entities), MutableIdRepository<E> where E: Identifiable


fun main() {
    class Entity(
        override val id: ID,
        val name: String
    ): Identifiable {
        override fun toString() = "$name ($id)"

    }

    class EntityBuilder(
        override val id: ID,
        var name: String = ""
    ): IdBuilder<Entity> {

        override fun build() = Entity(id, name)
        override fun toString() = "$name ($id)"
    }

    val resource: Resource<EntityBuilder> =  {
        sequenceOf("hello", "world", "i", "am", "a", "test")
            .mapIndexed { index, s -> EntityBuilder(index.toLong(), s) }
    }

    val t0: Transformation<EntityBuilder> = {
            e -> if (e.name.length <= 2) { null } else {e}
    }

    val t1: Transformation<EntityBuilder> = {
            e -> e.also { e.name += "_" }
    }


    val repo = MutableIdRepository.from(resource)

    repo.apply(t0)
    println(repo.getById(5L))
    repo.apply(t1)
    println(repo.getById(1L))

    val finished = repo.finish()
    println(finished.getById(1L))

    println(finished.getAll())
}
