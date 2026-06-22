package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.bandwidth
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.kilometers

data class BandwidthParameters(
    val poleRadius: Distance = 4.kilometers,
    val bDistance: Double = 0.5,
    val aDistance: Double = 5.0,
)
