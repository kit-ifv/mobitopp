package domain.synthesis

import edu.kit.ifv.populationsynthesis.synthesis.CompletePopulationSynthesis

/**
 * A wrapper around an existing synthesis that is defined over a different input, but is supposed to return another
 * output
 */
class ConvertingSynthesis<AREA, I, O>(
    private val originalSynthesis: CompletePopulationSynthesis<AREA, I>,
    private val converter: (I) -> O,
) : CompletePopulationSynthesis<AREA, O> {
    override fun synthesizeAll(): Map<AREA, List<O>> = originalSynthesis.synthesizeAll().mapValues { (_, v) ->
        v.map { converter(it) }
    }

    override fun synthesize(targetAreas: List<AREA>): Map<AREA, List<O>> =
        originalSynthesis.synthesize(targetAreas).mapValues { (_, v) ->
            v.map { converter(it) }
        }
}

fun <AREA, I, O> CompletePopulationSynthesis<AREA, I>.withConverter(
    converter: (I) -> O,
): ConvertingSynthesis<AREA, I, O> = ConvertingSynthesis(this, converter)
