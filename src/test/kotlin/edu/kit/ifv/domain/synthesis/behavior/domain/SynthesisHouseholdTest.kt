package edu.kit.ifv.domain.synthesis.behavior.domain
import assertNotContains
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus
import edu.kit.ifv.domain.shared.enums.household.HouseholdType
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.SynthesisHousehold
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.results.fastcsv.CsvIgnore
import edu.kit.ifv.domain.synthesis.results.fastcsv.writers.writeHouseholds
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
        val economicStatus: EconomicStatus = EconomicStatus.MIDDLE,
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
        listOf(hh).writeHouseholds(stringWriter)

        val expected: String = stringWriter.toString()
        assertContains(expected, "id;economicStatus;income;type;zoneID")
        assertNotContains(expected, "location")
    }
}
