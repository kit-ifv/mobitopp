package domain.synthesis

import kotlin.random.Random

@Suppress("SpacingAroundColon") // Seems to be a detekt version thing
fun interface AssignmentStep<in I, out O> {
    context(random: Random)
    fun assign(input: I): O
}
