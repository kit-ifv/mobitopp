package edu.kit.ifv.domain.simulation.behavior.modechoice

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.utils.units.AbsoluteTime
import kotlin.random.Random

/**
 * Provide an interface, that way projects can actually implement additional conditions onto the characteristics.
 * These should be the minimum available characteristics during mode choice, so maybe impedance and currentChoices
 * need to be dropped for a more generic implementation.
 * Jelle: impedance holds distance/duration/cost information which should always be used in mode choice!
 */
interface ModeChoiceCharacteristics {
    val person: PersonAgent
    val time: AbsoluteTime
    val origin: StandardLocation
    val destination: StandardLocation
    val currentChoices: Collection<Mode> // cache of filtered modes before mode choice
    val custom: Any?

    companion object {
        @Suppress("LongParameterList")
        operator fun invoke(
            person: PersonAgent,
            time: AbsoluteTime,
            origin: StandardLocation,
            destination: StandardLocation,
            currentChoices: Collection<Mode>,
            custom: Any?,
        ): ModeChoiceCharacteristics = ModeChoiceCharacteristicsImpl(
            person,
            time,
            origin,
            destination,
            currentChoices,
            custom,
        )
    }
}

/**
 * Characteristics are invariant within a discrete choice situation.
 */
data class ModeChoiceCharacteristicsImpl(
    override val person: PersonAgent,
    override val time: AbsoluteTime,
    override val origin: StandardLocation,
    override val destination: StandardLocation,
    override val currentChoices: Collection<Mode>,
    override val custom: Any? = null,
) : ModeChoiceCharacteristics {
    val random: Random get() = person.random
    fun with(choice: Mode) = ModeChoiceAlternative(
        person,
        time,
        origin,
        destination,
        choice,
    )
}

data class ModeChoiceAlternative( // TODO check if this can be deleted?
    val person: PersonAgent,
    val time: AbsoluteTime,
    val origin: StandardLocation,
    val destination: StandardLocation,
    val choice: Mode,
)
