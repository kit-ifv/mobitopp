package choicemodels

import BIELEFELD
import domain.data.Person
import domain.data.point
import domain.enums.Mode
import domain.enums.StandardMode
import domain.enums.ZoneClassification
import domain.location.ZoneLocation
import generateZones
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import syntheticsim.ControllableImpedance
import syntheticsim.testAttractivenessModel
import usecases.choicemodels.FakePlan
import usecases.choicemodels.ILegacyDestinationChoice
import usecases.choicemodels.LegacyDestinationChoice
import usecases.choicemodels.ModeFilter
import usecases.choicemodels.destinationchoice.ModernizedDestinationChoice
import utils.collections.subsets
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class DestinationChoiceUtilityTest : CompareTwoUtilityFunctions<ILegacyDestinationChoice>() {
    // Since destination choice does not get impedance as a parameter but a constructor I had to change the test architecture
    // to incorporate this...oversight
    override val impedanceOverride: ControllableImpedance = ControllableImpedance()
    var targetSet: Set<Mode> = setOf(
        StandardMode.CAR,
        StandardMode.PASSENGER,
        StandardMode.PEDESTRIAN,
        StandardMode.BIKE,
        StandardMode.PUBLICTRANSPORT
    )
    var filter: ModeFilter<Mode, Person> = ModeFilter { _, _ ->
        targetSet
    }

    @BeforeTest
    fun setup() {
        val zones = generateZones(10)
        targetSet = setOf(StandardMode.CAR, StandardMode.PEDESTRIAN)

        a = LegacyDestinationChoice(
            impedance = impedanceOverride,
            testAttractivenessModel,
            umlands = { loc -> (loc as ZoneLocation).zone.classification == ZoneClassification.OUTLYING_AREA },
            zones.toSet(),
            FakePlan,
            filter = filter

        )
        b = ModernizedDestinationChoice(impedanceOverride, testAttractivenessModel)
    }

    @ParameterizedTest
    @MethodSource("getStandardModeAvailabilities")
    fun testDifferentAvailabilities(set: Set<Mode>) {
        targetSet = set
        runTest({}, {}) {
            assertEquals(filter.filter(emptyList(), person), set)
        }
    }
    override val comparison: TestSimulation.(ILegacyDestinationChoice, ILegacyDestinationChoice) -> Unit = { a, b ->
        val expected = a.calculateU_destination(
            destination.point(BIELEFELD),
            person,
            origin.point(BIELEFELD),
            destination.point(BIELEFELD),
            nextActivity,
            previousActivity.endTime,
            filter.filter(availableModes, person),
            0.5
        )
        val actual = b.calculateU_destination(
            destination.point(BIELEFELD),
            person,
            origin.point(BIELEFELD),
            destination.point(BIELEFELD),
            nextActivity,
            previousActivity.endTime,
            filter.filter(availableModes, person),
            0.5
        )
        assertEquals(
            expected,
            actual,
            0.0001,
            message = "U(a) = $expected U(b) = $actual Difference: ${expected - actual}"
        )
    }
    companion object {
        @JvmStatic
        fun getStandardModeAvailabilities(): List<Set<Mode>> {
            val standardModes = setOf(
                StandardMode.CAR,
                StandardMode.PASSENGER,
                StandardMode.PEDESTRIAN,
                StandardMode.BIKE,
                StandardMode.PUBLICTRANSPORT
            )
            return standardModes.subsets()
        }
    }
}
