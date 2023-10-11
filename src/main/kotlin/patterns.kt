typealias ID = Long

interface Identifiable {
    val id: ID
}

interface Builder<E> {
    fun build(): E
}

interface IdentifiableBuilder<E> : Builder<E>, Identifiable where E: Identifiable

