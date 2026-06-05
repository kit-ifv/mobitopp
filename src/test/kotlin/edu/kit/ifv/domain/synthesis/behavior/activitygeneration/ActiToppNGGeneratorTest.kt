package edu.kit.ifv.domain.synthesis.behavior.activitygeneration
import edu.kit.ifv.domain.shared.enums.areatype.RegioStaR17
import edu.kit.ifv.domain.shared.enums.areatype.RegionType
import edu.kit.ifv.domain.shared.enums.areatype.SizebasedRegiostarClassification
import edu.kit.ifv.domain.shared.enums.areatype.ZoneRegionType
import edu.kit.ifv.domain.shared.enums.areatype.toSizebasedClassification
import edu.kit.ifv.domain.shared.enums.household.HouseholdType
import edu.kit.ifv.domain.shared.enums.legacyChoiceModelPurposes
import edu.kit.ifv.domain.shared.enums.person.Employment
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.attributes.StandardLocationAttributes
import edu.kit.ifv.domain.shared.location.jts.PointCreator
import edu.kit.ifv.domain.shared.location.road.RoadAccess
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.domain.synthesis.SynthesisHousehold
import edu.kit.ifv.domain.synthesis.attributes.household.MaximumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.household.MaximumHouseholdAttributesImpl
import edu.kit.ifv.domain.synthesis.attributes.person.MaximumPersonAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MaximumPersonAttributesImpl
import edu.kit.ifv.domain.synthesis.behavior.SmallestSurveyPerson
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.kilometers
import org.locationtech.jts.geom.Point
import kotlin.test.Test
import kotlin.test.assertEquals

class BetterLocation constructor(
    override val position: Point,
    zoneId: ZoneId,

    regionType: RegionType,

    sizeClassification: SizebasedRegiostarClassification =
        regionType.toRegioStaR17().toSizebasedClassification(),

) : StandardLocation {
    override val attributes: StandardLocationAttributes = object : StandardLocationAttributes {
        override val regionType: RegionType = regionType
        override val sizebasedRegiostarClassification: SizebasedRegiostarClassification = sizeClassification
        override val zoneId: ZoneId = zoneId
        override val roadAccess: RoadAccess = RoadAccess.INVALID
    }

    constructor(position: Point, zone: Zone<HasRegionType>, roadAccess: RoadAccess) : this(
        position,
        zone.id,
        zone.attributes.regionType,
    )

    companion object {
        fun fromPoint(point: Point) = BetterLocation(
            position = point,
            zoneId = ZoneId(-1),
            regionType = RegioStaR17.MEDIUM_CITY_METRO,
            sizeClassification = SizebasedRegiostarClassification.CITY,
        )

        fun wgs(x: Double, y: Double): BetterLocation = fromPoint(PointCreator.createWGS(x, y))
    }
}

class ActiToppNGGeneratorTest {

    private val hh = SynthesisHousehold<MaximumHouseholdAttributes, MaximumPersonAttributes>(
        surveyHouseholdId = 1,
        attributes = MaximumHouseholdAttributesImpl(
            income = 1000.euros,
            type = HouseholdType.UNDEFINED,
            householdSize = 42,
            year = 1984,
            areaTypeCode = 13,
            amountOfCars = 9001,
        ),
    ).apply {
        attributes.location = BetterLocation.wgs(10.0, 50.0)
    }

    private val generator =
        ActiToppNGGenerator<MaximumHouseholdAttributes, MaximumPersonAttributes>(legacyChoiceModelPurposes) {
            ZoneRegionType.DEFAULT
        }

    @Test
    fun runWithPeople() {
        val person = SmallestSurveyPerson(
            personId = 25,
            attributes = MaximumPersonAttributesImpl(
                age = 42,
                sex = Sex.MALE,
                distanceWork = 1.kilometers,
                distanceEducation = 2.kilometers,
                employment = Employment.UNEMPLOYED,
                birthYear = 24024,
                personNumber = -11111,
                hasBicycle = true,
                hasLicence = true,
            ),
        )
        hh.addMember(person, MaximumPersonAttributes::copy)

        val output = generator.generate(hh)
        assertEquals(1, output.size)
        assertEquals(47, output.first().size)
    }
}
