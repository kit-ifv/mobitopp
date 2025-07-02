package domain.shared.enums

import utils.Encodable
import utils.EnumDecodable

/**
 * The ZoneClassification distinguishes different parts of the simulated area:
 * study area: usually where a measure is implemented
 * extended study area: usually where effects of the measure might be visible
 * outlying are: rest
 *
 * (imported from legacy mobiTopp)
 * //TODO maybe convert to interface with legacy implementation? or is this a general concept?
 *
 * @property code integer code of zone classification
 */
enum class ZoneClassification(override val code: Int) : Encodable {
    STUDY_AREA(0),
    EXTENDED_STUDY_AREA(1),
    OUTLYING_AREA(2);

    override val description: String
        get() = name

    companion object : EnumDecodable<ZoneClassification>(ZoneClassification::class)
}
