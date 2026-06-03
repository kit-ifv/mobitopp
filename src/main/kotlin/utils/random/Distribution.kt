package utils.random

import kotlin.random.Random

/**
 * A distribution models a domain of values with different weights.
 * It allows to draw a random value from the modeled domain.
 *
 * @param T the generic type of the values in the distribution
 * @constructor Create empty Random variable
 */
interface Distribution<T> {
    val name: String

    /**
     * Draws a value from this distribution using the given [StochasticActor]'s random generator.
     *
     * @param actor the stochastic actor selecting from the distribution
     * @return a (weighted) random value from this distribution
     */
    fun drawValue(actor: StochasticActor): T = this.drawValue(actor.random.nextDouble())

    /**
     * Draws a value from this distribution using the given [kotlin.random.Random] generator.
     *
     * @param random the random generator used to draw a value
     * @return a (weighted) random value from this distribution
     */
    fun drawValue(random: Random): T = this.drawValue(random.nextDouble())

    /**
     * Draws a value from this distribution using the given random number.
     *
     * @param randomNum a random double number in [0, 1)
     * @return a (weighted) random value from this distribution
     */
    fun drawValue(randomNum: Double): T
}
