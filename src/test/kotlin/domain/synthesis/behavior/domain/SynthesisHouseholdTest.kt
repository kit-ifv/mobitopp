package domain.synthesis.behavior.domain

import assertNotContains
import domain.shared.location.StandardLocation
import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.HouseholdType
import domain.synthesis.results.fastcsv.CsvIgnore
import domain.synthesis.results.fastcsv.write
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.euros
import java.io.StringWriter
import kotlin.test.Test
import kotlin.test.assertContains

class SynthesisHouseholdTest {

    internal class TestAttributes(
        override val income: Currency,
        override val type: HouseholdType,
        @CsvIgnore
        override var location: StandardLocation,
        val economicStatus: EconomicStatus = EconomicStatus.MIDDLE
    ) : MinimumHouseholdAttributes {
        val zoneID get() = location.zoneId

        @CsvIgnore
        val x get() = location.position.x
    }

    @Test
    fun testCSVWrite() {
        val hh = SynthesisHousehold<TestAttributes, MinimumPersonAttributes>(
            attributes = TestAttributes(
                income = 1.euros,
                type = HouseholdType.UNDEFINED,
                location = StandardLocation.LOCATIONUNKNOWN,
            ),

        )

        val stringWriter = StringWriter()
        listOf(hh).write(stringWriter)

        val expected: String = stringWriter.toString()
        assertContains(expected, "id;economicStatus;income;type;zoneID")
        assertNotContains(expected, "location")
    }
}
