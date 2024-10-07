package usecases.choicemodels

import datastructure.StationaryAction
import domain.data.Person
import domain.location.ZoneLocation

data class LegacyModeChoiceParameters(
    val person: Person,
    val previous: StationaryAction,
    val next: StationaryAction,
    val from: ZoneLocation,
    val to: ZoneLocation
)
