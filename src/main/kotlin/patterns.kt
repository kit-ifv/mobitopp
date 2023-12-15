internal object GlobalIdCount {
    //TODO check id overflow and raise exception if it occurs
    var next: ULong = 0u
        get() = field++
        private set

}


@JvmInline
public value class ID<E> (

    val id: ULong //TODO
)

interface Identifiable<E> {
    // TODO think about this: if a subclass is identifiable, should its parent class also be identifiable
    // TODO -> in this case this could be an abstract class and implement equals and hash
    val id: ID<E>
}

fun <E> E.newId(): ID<E> {
    return ID<E>(GlobalIdCount.next)
}

// Ideensammlung:
//interface Identifiable2<K> {
//    val id: K
//}
//class VisumZone(val name: String): Identifiable2<String> {
//    override val id: String
//        get() = name
//}
//
//interface Entity<E>: Identifiable2<ID<E>> {
//    override val id: ID<E>
//        get() = TODO("Not yet implemented")
//}




interface Builder<E> {
    fun build(): E
}

interface IdentifiableBuilder<E> : Builder<E>, Identifiable<E> where E: Identifiable<E>

/**
 * An object is encodable if it can provide an integer based on the attributes present. In the future this could be
 * abstracted to a template type in case that the encoding might return a different type (such as Strings)
 */
fun interface Encodable {
    fun encode(): Int
}

/**
 * Similarly to an encoding a decoding interface allows to construct the underlying type by the provided integer code.
 * As the decoding logic can not be realistically dependent on an explicit object instantiation the logic must be bound
 * to a different object and cannot be added to a shared interface providing both encoding and decoding. This turns out
 * to be beneficial as both interfaces can be functional.
 *
 * This could also be abstracted to generic decoding types if the need arises
 */
fun interface Decodable<T: Encodable> {
    fun decode(i: Int): T
}

typealias CodePlan<R> = Decodable<R>
