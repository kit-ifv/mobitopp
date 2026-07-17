package edu.kit.ifv.domain.synthesis.results.binary

data class ActivitiesBinaryRecord(
    val personID: Int,
    val durationMinutes: Long,
    val startTimeMinutes: Long,
    val activityType: Int,
)

data class SynthesisCarBinaryRecord(val houseHoldID: Int, val carID: Long)

data class FixedDestinationBinaryRecord(val personID: Int, val activityType: Int, val zoneID: Long)