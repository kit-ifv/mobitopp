package domain.shared.datastructure.matrix.visum

import domain.shared.location.zone.ZoneId

interface VisumParser {
    fun getZoneIds(): Array<ZoneId>
    fun getArray(): DoubleArray
    operator fun component1() = getArray()
    operator fun component2() = getZoneIds()
}
