@file:Suppress("FunctionNameMaxLength")

package codegeneration

import MessageCalled
import StateCalled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import kotlin.test.assertEquals

class SimpleScope(val data: String)
class GenericScope<T>(val value: T)
class MixedScope(val value: Number, val data: String)

@MessageCalled("Hello", SimpleScope::class)
class HelloMessage(val data: String) {
    constructor(firstName: String, lastName: String) : this("$firstName $lastName")
}

class TestHelloMessage {

    @Test
    fun useGeneratedHelloType() {
        assertEquals(HelloMessage::class, Hello)
    }

    @Test
    fun useGeneratedHelloFunction() {
        val hello = SimpleScope("Bob").hello("Hello there!")
        assertInstanceOf<HelloMessage>(hello)
    }

    @Test
    fun useGeneratedHelloSecondConstructorFunction() {
        val hello = SimpleScope("Bob").hello("Hello", "there!")
        assertInstanceOf<HelloMessage>(hello)
    }

    @Test
    fun useGeneratedHelloFunctionWithDefaultValue() {
        val hello = SimpleScope("Bob").hello()
        assertInstanceOf<HelloMessage>(hello)
    }
}

@MessageCalled("GenericHello", SimpleScope::class)
class GenericHelloMessage<T : Any>(val data: String, val greetee: T)

class TestGenericHelloMessage {
    @Test
    fun useGeneratedGenericHelloType() {
        assertEquals(GenericHelloMessage::class, GenericHello)
    }

    @Test
    fun useGeneratedGenericHelloFunctionWithDefault() {
        val genericHello = SimpleScope("Bob").genericHello(greetee = 42L)
        assertInstanceOf<GenericHelloMessage<Long>>(genericHello)
    }
}

@MessageCalled("HelloToGeneric", GenericScope::class)
class GenericHelloOnGenericScope<T : Any>(val data: String, val value: T)

class TestGenericHelloOnGenericScope {

    @Test
    fun useGeneratedHelloToGenericType() {
        assertEquals(GenericHelloOnGenericScope::class, HelloToGeneric)
    }

    @Test
    fun useGeneratedHelloToGenericFunction() {
        val helloToGeneric = GenericScope(true).helloToGeneric("Test", 42L)
        assertInstanceOf<GenericHelloOnGenericScope<Boolean>>(helloToGeneric)
    }
}

@MessageCalled("BuildHelloFromScope", MixedScope::class)
class BuildGenericHelloFromScopeMessage<T : Number>(
    val scope: MixedScope,
    val data: String,
    val value: T,
    val name: String = scope::class.simpleName!!,
)

class TestBuildGenericHelloFromScopeMessage {

    @Test
    fun useGeneratedHelloToGenericType() {
        assertEquals(BuildGenericHelloFromScopeMessage::class, BuildHelloFromScope)
    }

    @Test
    fun useGeneratedBuildHelloFromScopeFunction() {
        val helloFromScope = MixedScope(42L, "Hello there!").buildHelloFromScope(value = 17L, name = "Bob")
        assertInstanceOf<BuildGenericHelloFromScopeMessage<Long>>(helloFromScope)
    }
}

@StateCalled(name = "Greeting", SimpleScope::class)
class GreetingState(val data: String)

class TestSimpleState {

    @Test
    fun useGeneratedGreetingType() {
        assertEquals(GreetingState::class, Greeting)
    }

    @Test
    fun useGeneratedGreetingFunction() {
        SimpleScope("Bob").greeting("Hello there!")
    }

    @Test
    fun useGeneratedGreetingFunctionWithDefault() {
        SimpleScope("Bob").greeting()
    }
}
