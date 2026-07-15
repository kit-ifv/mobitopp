package edu.kit.ifv.domain.simulation.behavior.availability.rules

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.simulation.agent.getBestCarOrNull
import edu.kit.ifv.domain.simulation.behavior.availability.CarResource
import edu.kit.ifv.domain.simulation.behavior.availability.rules.builder.AvailabilityByRuleBuilder
import edu.kit.ifv.domain.simulation.behavior.availability.rules.builder.default

fun AvailabilityByRuleBuilder.defaultCarAvailability(car: Mode) = availabilityOf(car).staticRule {
    person.hasLicense && person.household.cars.isNotEmpty()
}.providerRule {
    homeBasedVehicleRule()
}.resourceRule {
    characteristics.person.getBestCarOrNull()?.let { vehicle ->
        CarResource(car, vehicle)
    }
}

fun AvailabilityByRuleBuilder.defaultBikeRule(bike: Mode) = availabilityOf(bike).staticRule {
    person.hasBike
}.providerRule {
    homeBasedVehicleRule()
}.default()

fun AvailabilityByRuleBuilder.defaultSharingStationRules(sharing: Mode, pedestrian: Mode, impedance: Impedance) =
    availabilityOf(sharing).staticRule {
        person.sharingMemberships.any { it.mode == mode }
    }.providerRule {
        sharingProviders.checkOperatingHours()
            .stations()
            .checkAgentInFootZones()
            .checkAnyAvailable()
    }.resourceRule {
        sharingProviders.checkOperatingHours()
            .stations()
            .checkVehiclesAvailable()
            .checkAgentInFootZones()
            .selectStationByMinDistance(pedestrian, impedance)
    }

fun AvailabilityByRuleBuilder.defaultSharingFloatingRules(sharing: Mode, pedestrian: Mode, impedance: Impedance) =
    availabilityOf(sharing).staticRule {
        person.sharingMemberships.any { it.mode == mode }
    }.providerRule {
        sharingProviders.checkOperatingHours()
            .stations()
            .checkAgentInFootZones()
            .checkDestinationInFootZones()
            .checkAnyAvailable()
    }.resourceRule {
        sharingProviders.checkOperatingHours()
            .stations()
            .checkVehiclesAvailable()
            .checkAgentInFootZones()
            .selectMinDistOneWaySharing(pedestrian, impedance)
    }

fun AvailabilityByRuleBuilder.defaultDrtRules(drt: Mode) = availabilityOf(drt).staticRule {
    person.drtMemberships.any { it.mode == mode }
}.providerRule {
    drtProviders.checkOperatingHoursAndArea()
        .checkAnyAvailable()
}.resourceRule {
    drtProviders.checkOperatingHoursAndArea()
        .selectRideOfferByMinDuration()
}
