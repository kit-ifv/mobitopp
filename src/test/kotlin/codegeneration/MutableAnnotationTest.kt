package codegeneration

import Mutable

@Mutable
abstract class TestA {
    abstract val age: Int
    abstract val name: String
}

@Mutable
abstract class TestB(val a: MutableTestA) {

    abstract val size: Int
}
