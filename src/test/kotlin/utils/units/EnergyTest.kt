package utils.units

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds


class EnergyTest :
    GenericUnitTest<EnergyUnits, Energy>(
        EnergyUnits.values(),
        Int::toEnergy,
        Long::toEnergy,
        Double::toEnergy
    ) {
    @Test
    fun creation() {
        val energy = Energy.of(1.kilograms, 1.meters, 1.seconds)
        assertEquals(1.0, energy.rawValue)
    }
    @Test
    fun kilowatthour() {
        assertEquals(1.toEnergy(EnergyUnits.KILOWATTHOUR), 3600000.toEnergy(EnergyUnits.JOULE))
    }

}
