package usecases.choicemodels

import datastructure.StationaryAction
import domain.data.Person
import domain.enums.Mode
import domain.location.ZoneLocation

data class LegacyModeChoiceParameters(
    val person: Person,
    val previous: StationaryAction,
    val next: StationaryAction,
    val from: ZoneLocation,
    val to: ZoneLocation
)

data class ChoiceModelModes(
    val car: Mode,
    val passenger: Mode,
    val bike: Mode,
    val pedestrian: Mode,
    val publicTransport: Mode,
    val bikeSharing: Mode,
    val ridePooling: Mode,
    val carSharingFree: Mode,
    val carSharingStation: Mode,
    val taxi: Mode,
    val eScooter: Mode,
) {
    val options = setOf(
        car, passenger, bike, pedestrian, publicTransport, bikeSharing, ridePooling, carSharingStation,
        carSharingFree, taxi, eScooter
    )
}
