package edu.kit.ifv.domain.synthesis.behavior.activitygeneration
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.areatype.RegionType
import edu.kit.ifv.domain.shared.enums.person.Employment
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.mobitopp.actitoppNG.enums.AreaType
import edu.kit.ifv.mobitopp.actitoppNG.enums.Gender

/**
 * Adapter interface for translating between project-specific domain values and actiTopp enums.
 * Implementations are responsible for all enum mappings and should capture any project-specific
 * conventions or fallbacks (e.g., handling of unknown sex or region types).
 */
interface ActiToppAdapter {
    fun decodeActivityType(actiToppType: ActitoppActivityType): ActivityType
    fun encodeRegionType(regionType: RegionType): AreaType
    fun encodeEmployment(employment: Employment): ActitoppEmployment
    fun encodeSex(sex: Sex): Gender
}
