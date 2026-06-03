package utils.random

import kotlin.random.Random

/**
 * Stochastic actor describes actors taking part in stochastic processes.
 * Therefore, they must provide a random generator.
 */
interface StochasticActor {
    val random: Random
}
