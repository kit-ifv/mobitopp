

object GlobalIdCount {
    //TODO check id overflow and raise exception if it occurs, also maybe we can start ids at 0??
    var next: ULong = 0u
        get() = field++
        private set

}

@JvmInline
value class ID<E> (
    val id: ULong //TODO
)

interface Identifiable<E> {
    //TODO think about this: if a subclass is identifiable, should its parent class also be identifiable
    // TODO -> in this case this could be an abstract class and implement equals and hash
    val id: ID<E>
}

fun <E> E.newId(): ID<E> {
    return ID<E>(GlobalIdCount.next)
}




interface Builder<E> {
    fun build(): E
}

interface IdentifiableBuilder<E> : Builder<E>, Identifiable<E> where E: Identifiable<E>
