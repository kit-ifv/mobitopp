package domain.synthesis.behavior.activitygeneration

import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.enums.areatype.SizebasedRegiostarClassification
import domain.shared.enums.areatype.ZoneRegionType
import domain.shared.enums.areatype.toSizebasedClassification
import domain.shared.enums.legacyChoiceModelPurposes
import domain.shared.location.StandardLocation
import domain.shared.location.attributes.StandardLocationAttributes
import domain.shared.location.jts.PointCreator
import domain.shared.location.road.RoadAccess
import domain.shared.location.zone.Zone
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.attributes.HasRegionType
import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.attributes.household.MaximumHouseholdAttributesImpl
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.attributes.person.MaximumPersonAttributesImpl
import domain.synthesis.behavior.SmallestSurveyPerson
import domain.synthesis.data.household.HouseholdType
import domain.synthesis.data.person.Employment
import domain.synthesis.data.person.Sex
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
