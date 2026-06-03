package domain.synthesis

import kotlin.random.Random

fun interface AssignmentStep<in I, out O> {
    context(random: Random)
    fun assign(input: I): O
}
