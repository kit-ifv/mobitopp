package choicemodels

import DebugImpedance
import OTHER_TEST_ZONE
import TEST_ACTIVITY
import TEST_ZONE
import domain.enums.StandardMode
import domain.location.Location
import domain.location.ZoneLocation
import domain.location.ZoneLocationImpl
import testPerson
import usecases.AttractivenessModel
import usecases.choicemodels.LegacyDestinationChoice
import utils.units.AbsoluteTime
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours

class DestinationChoiceTest {
    val targetZones: Set<ZoneLocation> = listOf(TEST_ZONE, OTHER_TEST_ZONE).map {
        ZoneLocationImpl(
            it.centroid.coordinate,
            it
        )
    }.toSet()

    @Test
    fun chooseDestination() {
        val impedance = DebugImpedance()
        val attractivities = AttractivenessModel { _, _ -> 1.0 }

        val umlands: (Location) -> Boolean = { true }
        val testZones = setOf(TEST_ZONE, OTHER_TEST_ZONE)
        val d = LegacyDestinationChoice(impedance, attractivities, umlands, testZones, StandardMode)

        d.run {
            targetZones.selectDestination(
                testPerson,
                TEST_ACTIVITY,
                TEST_ACTIVITY,
                StandardMode.entries.toSet(),
                0.5
            )
        }
        d.calculateU_destination(
            OTHER_TEST_ZONE.centroid,
            testPerson,
            TEST_ZONE.centroid,
            OTHER_TEST_ZONE.centroid,
            TEST_ACTIVITY,
            AbsoluteTime.START + 4.hours,
            StandardMode.entries.toSet(),
            0.5
        )
    }
}
