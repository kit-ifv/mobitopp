package datastructure.matrix

import domain.data.ZoneId

interface IVisumParser {
    // Custom getter are not allowed with lateinit -.- therefore I wrote this. Take that kotlin compiler
    fun getZoneIds(): Array<ZoneId>

    // Custom getter are not allowed with lateinit -.- therefore I wrote this. Take that kotlin compiler
    // TODO use by lazy { } instead of lateinit
    fun getArray(): Array<Double>
}
