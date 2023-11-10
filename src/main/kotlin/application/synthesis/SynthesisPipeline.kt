package application.synthesis


interface Context {
    val names: List<String>
    val numbers: List<Int>
}

class MutContext: Context {
    override val names: MutableList<String> = mutableListOf()
    override val numbers: MutableList<Int> = mutableListOf()
}


interface Step<I,O> {
    fun execute(input: I): O
}

class FunctionStep<I, O>(
    private val transformation: (I) -> O
): Step<I, O> {
    override fun execute(input: I): O = transformation(input)
}


open class Pipeline<E>(
     protected val source: () -> E
): Step<Unit, E> {

    constructor(element: E): this({element})

    fun execute() = execute(Unit)

    override fun execute(input: Unit): E = source()

    fun <P> addStep(step: Step<E, P>): Pipeline<P> = Pipeline { step.execute(source()) }

    fun <I, O> asStep(merge: (I, E) -> O): Step<I, O> = FunctionStep {input -> merge(input, source())}

    fun build(): () -> E = source
}


