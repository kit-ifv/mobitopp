package choicemodels

import BIELEFELD
import domain.data.Person
import domain.data.point
import domain.enums.Mode
import domain.enums.ZoneClassification
import generateZones
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import syntheticsim.ControllableImpedance
import syntheticsim.testAttractivenessModel
import usecases.LegacyMode
import usecases.choicemodels.ChoiceFilter
import usecases.choicemodels.ILegacyDestinationChoice
import usecases.choicemodels.LegacyDestinationChoice
import usecases.choicemodels.destinationchoice.ModernizedDestinationChoice
import usecases.legacyChoiceModelModes
import usecases.legacyChoiceModelPurposes
import utils.collections.subsets
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class DestinationChoiceUtilityTest : CompareTwoUtilityFunctionsOld<ILegacyDestinationChoice>() {
    // Since destination choice does not get impedance as a parameter but a constructor I had to change the test architecture
    // to incorporate this...oversight
    override val impedanceOverride: ControllableImpedance = ControllableImpedance()
    var targetSet: Set<Mode> = setOf(
        LegacyMode.CAR,
        LegacyMode.PASSENGER,
        LegacyMode.PEDESTRIAN,
        LegacyMode.BIKE,
        LegacyMode.PUBLICTRANSPORT,
    )
    var filter: ChoiceFilter<Mode, Person> = ChoiceFilter { _, _ ->
        targetSet
    }

    @BeforeTest
    fun setup() {
        val zones = generateZones(10)
        targetSet = setOf(LegacyMode.CAR, LegacyMode.PEDESTRIAN)

        a = LegacyDestinationChoice(
            impedance = impedanceOverride,
            testAttractivenessModel,
            umlands = { loc -> loc.requireZone().classification == ZoneClassification.OUTLYING_AREA },
            zones.toSet(),
            legacyChoiceModelModes,
            legacyChoiceModelPurposes,
            filter = filter

        )
        b = ModernizedDestinationChoice(
            impedanceOverride,
            testAttractivenessModel,
            purposes = legacyChoiceModelPurposes,
            modes = legacyChoiceModelModes,
            zones = zones.toSet()
        )
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
                LegacyMode.CAR,
                LegacyMode.PASSENGER,
                LegacyMode.PEDESTRIAN,
                LegacyMode.BIKE,
                LegacyMode.PUBLICTRANSPORT
            )
            return standardModes.subsets()
        }
    }
}
