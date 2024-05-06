package domain.enums

import utils.CodePlan
import utils.Encodable

/**
 * The ZoneClassification distinguishes different parts of the simulated area:
 * study area: usually where a measure is implemented
 * extended study area: usually where effects of the measure might be visible
 * outlying are: rest
 *
 * (imported from legacy mobiTopp)
 *
 * @property code integer code of zone classification
 */
enum class ZoneClassification(private val code: Int) : Encodable {
    STUDY_AREA(0),
    EXTENDED_STUDY_AREA(1),
    OUTLYING_AREA(2);

    override fun encode() = this.code

    companion object : CodePlan<ZoneClassification> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
    }
}
