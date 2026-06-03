package domain.synthesis.results.legacy

import domain.shared.location.ZonedRoadAccessLocationRecord

/**
 * Converts the Location to the standard representation found in legacy mobitopp input files which is the format
 * (lat, lon: roadId, accessShare)
 */
fun ZonedRoadAccessLocationRecord.legacyStringRepresentation(): String =
    "(${position.y}, ${position.x}: ${roadAccess.roadId}, ${roadAccess.position})"
