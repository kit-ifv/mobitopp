package edu.kit.ifv.domain.shared.datastructure.matrix.visum
import edu.kit.ifv.domain.shared.location.zone.ZoneId

interface VisumParser {
    fun getZoneIds(): Array<ZoneId>
    fun getArray(): DoubleArray
    operator fun component1() = getArray()
    operator fun component2() = getZoneIds()
}
