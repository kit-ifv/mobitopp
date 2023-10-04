package utils.units


import kotlin.test.Test
import kotlin.test.assertEquals


class AreaTest: GenericUnitTest<AreaUnits, Area>(AreaUnits.values(), Int::toArea, Long::toArea, Double::toArea) {
    @Test
    fun creation() {
        val area = Area.ofRectangle(1.meters, 1.meters)

        val test = area.toDouble(AreaUnits.SQUARE_KILOMETERS)
        assertEquals(0.000001, test)

    }
    @Test
    fun noCrossContamination() {
        val area = 1.toArea(AreaUnits.SQUARE_METERS)

        val test = area + area
        return
//        val distance = 1.toDistance(DistanceUnit.METERS)
//        val energy = 1.toEnergy(EnergyUnits.JOULE)
//        val mass = 1.toMass(MassUnit.KILOGRAM)
//        val power = 1.toPower(PowerUnit.KILOWATTS)
//        val temp = 1.toTemperature(TemperatureUnit.FAHRENHEIT)
    }
}
