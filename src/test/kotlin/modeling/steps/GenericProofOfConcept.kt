package modeling.steps

import java.io.BufferedReader
import java.io.File

@JvmInline
internal value class TestId<out E>(val id: Long)

internal interface Builda<out E> {
    fun build(): E
}


internal interface Identifiable<I> {
    val id: I
}

internal typealias ParentId = TestId<Parent>

internal open class Parent(override val id: ParentId): Identifiable<ParentId>
internal class Child(id: TestId<Child>): Parent(id)
internal typealias OtherId = TestId<Other>

internal class Other(override val id: OtherId): Identifiable<OtherId>

internal class Container<E, I>(
    elements: List<E>
) where E:Identifiable<I> {
    private val map: Map<I, E> = elements.associateBy { it.id }
    fun getById(id: I): E? = map[id]
}

fun main() {
    val p = Parent(TestId(1))
    val c = Child(TestId(2))
    val o = Other(TestId(3))

    val cP = Container(listOf(p, c))
    val cC = Container(listOf(c))
    val cO = Container(listOf(o))

    cP.getById(c.id)
    cC.getById(p.id)
    cO.getById(o.id)
    //cP.getById(o.id)
}


internal interface Source<out E> {
    val elements: Sequence<E>

    //fun map(map: (E) -> E?): Resource<E>

}

internal interface Repo<out E, I>: Source<E> where E: Identifiable<I> {
    fun getById(id: I): E?

}

internal class MapRepo<out E, I>(
    elements: Sequence<E>
): Repo<E, I> where E: Identifiable<I> {
    val map: Map<I, E> = elements.associateBy { it.id }
    override fun getById(id: I): E? = map[id]

    override val elements: Sequence<E>
        get() = map.values.asSequence()

}

internal interface BuildRepo<B, out E, I>: Repo<E, I>
        where B: Builda<E>, E: Identifiable<I>
{
    val builders: List<B>
    val res: Repo<E, I>?

    fun map(map: (B) -> B?): BuildRepo<B,E,I>
    fun filter(map: (B) -> Boolean): BuildRepo<B,E,I>
    fun add(
        other: B
    ): BuildRepo<B,E,I>

    fun merge(other: BuildRepo<B, *, *>)
    fun build(): Repo<E, I>
}

internal fun testAdd(repo: BuildRepo<Builda<Parent>, Parent, ParentId>) {
    val cB = Child(TestId(3)).asBuilder()
    repo.add(cB)
}

internal fun <E> E.asBuilder() = object : Builda<E> {
    override fun build(): E = this@asBuilder
}

internal fun <S, E> S.asBuilders() where S: Sequence<E> = this.map { it.asBuilder() }

internal fun <I, E> I.asBuilders() where I: Iterable<E> = this.map { it.asBuilder() }


internal interface Ctxt {
    val parents: Repo<Parent, ParentId>
}

internal interface ParentCtxt {
    val parents: BuildRepo<Builda<Parent>, Parent, ParentId>
}
internal interface ChildCtxt {
    val parents: BuildRepo<Builda<Child>, Child, ParentId>
}


internal class ContextImpl(
    override val parents: BuildRepo<Builda<Parent>, Parent, ParentId>
) : Ctxt, ParentCtxt

internal class ChildContextImpl(
    override val parents: BuildRepo<Builda<Child>, Child, ParentId>
) : Ctxt, ChildCtxt

internal class Synth<C>(val ctxt: C) where C: Ctxt

internal fun <S, C> S.parentOp() where S: Synth<C>, C: ParentCtxt {
    val c = Child(TestId(1))
    val p = Parent(TestId(2))
    this.ctxt.parents.add(c.asBuilder())
    this.ctxt.parents.add(p.asBuilder())
}

internal fun <S, C> S.childOp() where S: Synth<C>, C: ChildCtxt {
    val c = Child(TestId(1))
    val p = Parent(TestId(2))
    this.ctxt.parents.add(c.asBuilder())
}








fun interface ReaderFactory {
    fun read(file: File): BufferedReader
}

val basicReader = ReaderFactory { it.bufferedReader() }
val compressedReader = ReaderFactory {
    when(it.extension) {
        "bz2" -> it.bufferedReader() //bzip2 magic here
        else -> it.bufferedReader()
    }
}


abstract class Parser<E>(
    val reader: ReaderFactory = compressedReader
) {
    abstract fun parse(source: File): E
}

class ParserA(
    reader: ReaderFactory = compressedReader
): Parser<String>(reader) {

    override fun parse(source: File): String {
        //check specific extension
        require(source.extension == "net")

        val res = reader.read(source).lineSequence().joinToString(";")

        return res
    }

}

class ParserB(
    reader: ReaderFactory = compressedReader
): Parser<Int>(reader) {
    override fun parse(source: File): Int {
        require(source.extension == "ints")

        //check specific extension
        return reader.read(source).lineSequence()
            .map { it.toInt() }.sum()
    }

}