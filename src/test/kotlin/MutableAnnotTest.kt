

@Mutable("IntermediateMutTestA")
abstract class TestA {
    abstract val age: Int
    abstract val name: String
}

class MutableTestA(scope: IntermediateMutTestA.() -> Unit) : IntermediateMutTestA(scope)

@Mutable
abstract class TestB(
    val a: MutableTestA
) {

    abstract val size: Int
}
