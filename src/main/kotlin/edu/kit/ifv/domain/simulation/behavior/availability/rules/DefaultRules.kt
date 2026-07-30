package edu.kit.ifv.domain.simulation.behavior.availability.rules

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.simulation.agent.getBestCarOrNull
import edu.kit.ifv.domain.simulation.behavior.availability.BikeResource
import edu.kit.ifv.domain.simulation.behavior.availability.CarResource
import edu.kit.ifv.domain.simulation.behavior.availability.rules.builder.AvailabilityByRuleBuilder

/**
 * Adds the default car availability rule to the rules builder.
 * Static availability requires a license and at least one car in the household.
 * As a home-based mode, agents must be at home to start a tour using this mode. The household is the vehicle provider.
 * Resource availability depends on the availability of a private car in the household.
 */
fun AvailabilityByRuleBuilder.defaultCarAvailability(car: Mode) = availabilityOf(car).staticRule {
    person.hasLicense && person.household.cars.isNotEmpty()
}.providerRule {
    homeBasedVehicleRule(useProvider = true)
}.resourceRule {
    characteristics.person.getBestCarOrNull()?.let { vehicle ->
        CarResource(car, vehicle)
    }
}

/**
 * Adds the default bike availability rule to the builder.
 * Static availability requires the person to have a bike.
 * As a home-based agents must be at home to start a tour using this mode.
 * Bikes are not shared in the household so no provider is used.
 * If at home, bike is always available.
 */
fun AvailabilityByRuleBuilder.defaultBikeRule(bike: Mode) = availabilityOf(bike).staticRule {
    person.hasBike
}.providerRule {
    homeBasedVehicleRule(useProvider = false)
}.resourceRule {
    BikeResource(bike)
}

/**
 * Adds the default station-based sharing availability rules to the builder.
 * Static availability requires membership in the sharing service for the mode.
 * Provider availability checks for operating hours, station existence,
 * and whether the agent is in walking distance of a station.
 * Stations in walking distance are used as sharing vehicle providers.
 * Resource availability selects a vehicle from the nearest station with available vehicles.
 */
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

/**
 * Adds the default free-floating sharing availability rules to the builder.
 * Static availability requires membership in the sharing service for the mode.
 * Provider availability checks for operating hours, station (zone) existence,
 * and whether both agent and destination are in walking distance of a station/zone.
 * Start stations/zones in walking distance are used as sharing vehicle providers.
 * Resource availability selects a vehicle and destination zone based (both in walking distance)
 * on minimum total travel distance and a vehicle from the start zone.
 */
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

/**
 * Adds the default ride pooling (DRT) availability rules to the builder.
 * Static availability requires membership in the DRT service for the mode.
 * Provider availability checks if any DRT provider operates at the current time in the required area.
 * Resource availability requests and selects the best ride offer based on minimum trip duration.
 */
fun AvailabilityByRuleBuilder.defaultDrtRules(drt: Mode) = availabilityOf(drt).staticRule {
    person.drtMemberships.any { it.mode == mode }
}.providerRule {
    drtProviders.checkOperatingHoursAndArea()
        .checkAnyAvailable()
}.resourceRule {
    drtProviders.checkOperatingHoursAndArea()
        .selectRideOfferByMinDuration()
}
