package data

import Mutable
import kotlin.properties.Delegates

@Mutable
class Examplum {
}
@Mutable
data class Data(val i: Int)
@Mutable
class ClassWithList(private val text: List<String>)
@Mutable
class ClassWithMap(private val text: Map<String, String>)
@Mutable
class ClassWithSet(private val text: Set<String>)

@Mutable
class ClassWithSeet(private val text: Set<String>)

@Mutable
class ClassWithObject(private val o: ClassWithList)

@Mutable
class ClassiWithObjectInCollection(private val t: Collection<ClassWithList>)


class Builder {
    lateinit var text: String

}

