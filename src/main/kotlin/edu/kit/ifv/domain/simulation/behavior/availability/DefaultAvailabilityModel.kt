package edu.kit.ifv.domain.simulation.behavior.availability

import edu.kit.ifv.domain.shared.behavior.ChoiceModelModes
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.simulation.agent.getBestCarOrNull
import edu.kit.ifv.domain.simulation.behavior.availability.rules.builder.availabilityRules
import edu.kit.ifv.domain.simulation.behavior.availability.rules.builder.default

/**
 * Creates a default [ModeAvailabilityModel] using the provided [ChoiceModelModes].
 *
 * @param modes the modes to be used in the availability model
 * @param beamedModes additional modes that are always available (beamed)
 * @param impedance the impedance used for distance-based resource selection
 * @return a [ModeAvailabilityModel] with default rules for all modes
 */
fun defaultAvailabilityModel(modes: ChoiceModelModes, vararg beamedModes: Mode, impedance: Impedance) =
    defaultAvailabilityModel(
        pedestrian = modes.pedestrian,
        bike = modes.bike,
        car = modes.car,
        passenger = modes.passenger,
        publicTransport = modes.publicTransport,
        carSharingFree = modes.carSharingFree,
        carSharingStation = modes.carSharingStation,
        bikeSharingOneWay = modes.bikeSharing,
        ridePooling = modes.ridePooling,
        beamedModes = beamedModes,
        impedance = impedance,
    )

/**
 * Creates a default [ModeAvailabilityModel] with explicitly provided [Mode]s.
 *
 * This function defines the default rules for various transport modes:
 * - [pedestrian], [passenger], [publicTransport], and [beamedModes] are always available.
 * - [bike] requires the person to have a bike and is home-based.
 * - [car] requires a license, a car in the household, and is home-based.
 * - [carSharingFree], [carSharingStation], [bikeSharingOneWay] require membership and availability from providers.
 * - [ridePooling] (DRT) requires membership and availability from DRT providers.
 *
 * @param pedestrian the pedestrian mode
 * @param bike the bike mode
 * @param car the car mode
 * @param passenger the car passenger mode
 * @param publicTransport the public transport mode
 * @param carSharingFree the free-floating car sharing mode
 * @param carSharingStation the station-based car sharing mode
 * @param bikeSharingOneWay the one-way bike sharing mode
 * @param ridePooling the ride pooling (DRT) mode
 * @param beamedModes additional modes that are always available
 * @param impedance the impedance used for distance-based resource selection
 * @return a configured [ModeAvailabilityModel]
 */
@Suppress("LongMethod")
fun defaultAvailabilityModel(
    pedestrian: Mode,
    bike: Mode,
    car: Mode,
    passenger: Mode,
    publicTransport: Mode,
    carSharingFree: Mode,
    carSharingStation: Mode,
    bikeSharingOneWay: Mode,
    ridePooling: Mode,
    vararg beamedModes: Mode,
    impedance: Impedance,
): ModeAvailabilityModel = availabilityRules {
    simpleAvailabilityOf(pedestrian)

    availabilityOf(bike).staticRule {
        person.hasBike
    }.providerRule {
        homeBasedVehicleRule(useProvider = false)
    }.resourceRule {
        BikeResource(bike)
    }

    availabilityOf(car).staticRule {
        person.hasLicense && person.household.cars.isNotEmpty()
    }.providerRule {
        homeBasedVehicleRule(useProvider = true)
    }.resourceRule {
        characteristics.person.getBestCarOrNull()?.let { vehicle ->
            CarResource(car, vehicle)
        }
    }

    simpleAvailabilityOf(passenger)
    simpleAvailabilityOf(publicTransport)

    availabilityOf(carSharingFree).staticRule {
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

    availabilityOf(carSharingStation).staticRule {
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

    availabilityOf(bikeSharingOneWay).staticRule {
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

    availabilityOf(ridePooling).staticRule {
        person.drtMemberships.any { it.mode == mode }
    }.providerRule {
        drtProviders.checkOperatingHoursAndArea()
            .checkAnyAvailable()
    }.resourceRule {
        drtProviders.checkOperatingHoursAndArea()
            .selectRideOfferByMinDuration()
    }

    for (m in beamedModes) {
        simpleAvailabilityOf(m)
    }
}
