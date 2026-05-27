package domain.synthesis.behavior.activityGeneration

import domain.shared.enums.areatype.ZoneRegionType
import domain.shared.enums.legacyChoiceModelPurposes
import domain.shared.location.BetterLocation
import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.attributes.household.MaximumHouseholdAttributesImpl
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.attributes.person.MaximumPersonAttributesImpl
import domain.synthesis.behavior.SmallestSurveyPerson
import domain.synthesis.data.Employment
import domain.synthesis.data.HouseholdType
import domain.synthesis.data.Sex
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.kilometers
import kotlin.test.Test
import kotlin.test.assertEquals

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
