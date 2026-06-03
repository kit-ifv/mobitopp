package domain.synthesis.behavior.activitygeneration

import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.enums.ActivityType
import domain.shared.enums.areatype.RegionType
import domain.shared.enums.areatype.ZoneRegionType
import domain.shared.enums.person.Employment
import domain.shared.enums.person.Sex
import edu.kit.ifv.mobitopp.actitoppNG.enums.AreaType
import edu.kit.ifv.mobitopp.actitoppNG.enums.Gender

/**
 * Default implementation of [ActiToppAdapter].
 * Customization points:
 * [unknownSexResolution]: defines the fallback [edu.kit.ifv.mobitopp.actitoppNG.enums.Gender] for non-binary/unknown [Sex].
 * [converter]: maps project [domain.shared.enums.areatype.RegionType] to [domain.shared.enums.areatype.ZoneRegionType]
 * before converting to actiTopp [edu.kit.ifv.mobitopp.actitoppNG.enums.AreaType].
 * Mapping notes:
 * Employment: Some project values map to DEFINITELY_UNKNOWN when no direct actiTopp equivalent exists
 * (e.g., EDUCATION, INFANT). Review if this is acceptable for your use case.
 * ActivityType decoding: actiTopp types are mapped to project purposes provided via [purposes].
 * TRANSPORT maps to purposes.service; confirm that this aligns with your domain semantics.
 */
class StandardActiToppAdapter(
    private val purposes: ChoiceModelPurposes,
    val unknownSexResolution: (Sex) ->
    Gender = { Gender.FEMALE },
    val converter: (RegionType) -> ZoneRegionType,
) : ActiToppAdapter {
    override fun encodeSex(sex: Sex): Gender = when (sex) {
        Sex.MALE -> Gender.MALE
        Sex.FEMALE -> Gender.FEMALE
        else -> unknownSexResolution(sex)
    }

    override fun encodeRegionType(regionType: RegionType): AreaType = when (converter(regionType)) {
        ZoneRegionType.RURAL -> AreaType.RURAL
        ZoneRegionType.PROVINCIAL -> AreaType.PROVINCIAL
        ZoneRegionType.CITYOUTSKIRT -> AreaType.CITYOUTSKIRT
        ZoneRegionType.METROPOLITAN -> AreaType.METROPOLITAN
        ZoneRegionType.CONURBATION -> AreaType.CONURBATION
        ZoneRegionType.DEFAULT -> AreaType.UNKNOWN
    }

    @Suppress("CyclomaticComplexMethod")
    override fun encodeEmployment(employment: Employment): ActitoppEmployment = when (employment) {
        Employment.UNKNOWN -> ActitoppEmployment.DEFINITELY_UNKNOWN
        Employment.FULLTIME -> ActitoppEmployment.FULLTIME
        Employment.PARTTIME -> ActitoppEmployment.PARTTIME
        Employment.MARGINAL -> ActitoppEmployment.MARGINAL
        Employment.UNEMPLOYED -> ActitoppEmployment.UNOCCUPIED
        Employment.STUDENT -> ActitoppEmployment.STUDENT
        Employment.STUDENT_PRIMARY -> ActitoppEmployment.STUDENT_PRIMARY
        Employment.STUDENT_SECONDARY -> ActitoppEmployment.STUDENT_SECONDARY
        Employment.STUDENT_TERTIARY -> ActitoppEmployment.STUDENT_TERTIARY
        Employment.EDUCATION -> ActitoppEmployment.DEFINITELY_UNKNOWN
        Employment.HOMEKEEPER -> ActitoppEmployment.HOUSEKEEPER
        Employment.RETIRED -> ActitoppEmployment.RETIRED
        Employment.INFANT -> ActitoppEmployment.DEFINITELY_UNKNOWN
        Employment.NONE -> ActitoppEmployment.UNOCCUPIED
    }

    override fun decodeActivityType(actiToppType: ActitoppActivityType): ActivityType = when (actiToppType) {
        ActitoppActivityType.WORK -> purposes.work
        ActitoppActivityType.EDUCATION -> purposes.education
        ActitoppActivityType.LEISURE -> purposes.leisure
        ActitoppActivityType.SHOPPING -> purposes.shopping
        ActitoppActivityType.TRANSPORT -> purposes.service
        ActitoppActivityType.HOME -> purposes.home
    }
}
