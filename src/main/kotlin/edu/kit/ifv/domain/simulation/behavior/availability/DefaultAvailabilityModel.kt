package edu.kit.ifv.domain.simulation.behavior.availability

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.simulation.agent.getBestCarOrNull
import edu.kit.ifv.domain.simulation.behavior.availability.rules.builder.availabilityRules
import edu.kit.ifv.domain.simulation.behavior.availability.rules.builder.default

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
        homeBasedVehicleRule()
    }.default()

    availabilityOf(car).staticRule {
        person.hasLicense && person.household.cars.isNotEmpty()
    }.providerRule {
        homeBasedVehicleRule()
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