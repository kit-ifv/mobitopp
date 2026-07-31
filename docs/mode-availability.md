# Mode Availability System

The mode availability system in mobitopp determines which transport modes are available to a person in a given situation. It uses a three-stage approach to efficiently filter alternatives and manage resource locking in parallel simulations.

## Three Levels of Availability

Availability is evaluated in three sequential stages, each building on the previous one:

### 1. Static Availability
Static availability refers to the modes generally available to a person based on their long-term attributes. It does not depend on the current time, location, or destination.
- **Criteria:** Age, driving license ownership, vehicle ownership (e.g., owning a bike), memberships (e.g., car sharing).
- **Goal:** Quickly filter out modes that the person can never use.

### 2. Provider Availability
Provider availability checks if a mode is available in the agent's current situation, considering abstract resource providers but not yet individual resources. This can be used e.g. during destination choice to get a fast approximation of vehicle availability when computing the utility for a large number of destination zones.
- **Criteria:** Current time (operating hours), current location (is a sharing station nearby?), destination (can I drop off the vehicle there?), and mode-locking (is the agent already using a different vehicle?).
- **Goal:** Identify which modes are potentially available and which resource providers (e.g., specific sharing stations or DRT services) can be asked for individual resource availability. In a multithreaded (parallel) simulation, the list of resource providers can be locked to safely check the dynamic resource availability.

### 3. Resource Availability
Resource availability is the final check that considers specific, individual resources. This can be used when computing the mode choice.
- **Criteria:** Is there a specific car available in the household? Is there at least one bike left at the bike sharing station? What is the best DRT offer for this trip?
- **Goal:** Select a specific `ModeResource` (like a vehicle or a booking) that can be used for the trip.

## ModeResource

A `ModeResource` represents transport mode including all physical or virtual resources required to use it (e.g. shared vehicle, drt ride offer).
It handles the lifecycle of the resource through `startTrip` and `endTrip` hooks.

### Provided Implementations

- **NoResourceMode**: Used for modes that don't require a specific individual resource tracker (e.g., Pedestrian, Public Transport). It may still handle "vehicle take-along" logic which requires a mode tp be used until the agent returns to home.
- **CarResource**: Represents a private car from a household. It tracks the car's state (Parked, In Use), and the current driver. It ensures the car is used until the agent return to home.
- **BikeResource**: Represents a private bike. Similar to a car, but simpler as it doesn't track vehicle states and current drive. This is because this ModeResource considers bikes as not shared (Shared household bikes can be modeled using custom ModeResources). This bike resource is used until the agent returns home.
- **SharingStationResource**: Used for station-based sharing. It handles taking a vehicle from a specific station at the start and returning it to a station at the end.
- **SharingFreeResource**: Used for free-floating sharing (each zone = one 'station') or one-way station based sharing. It manages taking a vehicle from a start zone/station and returning it to a destination zone/station.
- **PoolingResource**: Represents a booking in a Ride Pooling (DRT) service. It holds the `DrtOffer` and manages the `DrtRide` lifecycle.

---

## Defining Custom Availability Rules

Custom availability rules are defined using a type-safe Kotlin DSL. The entry point is the `availabilityRules` block.

```kotlin
val myModel = availabilityRules {
    availabilityOf(Mode.CAR)
        .staticRule { 
            // StaticRuleScope: access to 'person' and 'mode'
            person.hasLicense && person.household.cars.isNotEmpty() 
        }
        .providerRule { 
            // ProviderRuleScope: access to 'agent', 'time', 'destination', etc.
            homeBasedVehicleRule(useProvider = true) 
        }
        .resourceRule { 
            // ResourceRuleScope: access to 'characteristics'
            characteristics.person.getBestCarOrNull()?.let { car ->
                CarResource(mode, car)
            }
        }
}
```

### Simple Availability
For modes that are always available and don't require specific resources (like walking), use:
```kotlin
simpleAvailabilityOf(Mode.PEDESTRIAN)
```

---

## DSL Scopes and Helper Functions

The DSL provides several scopes with properties and helper functions to simplify rule definition.

### StaticRuleScope
Used within `.staticRule { ... }`.
- `mode`: The transport mode being checked.
- `person`: The `IPerson` for whom to check availability.

### AgentRuleScope (Common to Provider and Resource Scopes)
Provides access to basic simulation data and filtering for sharing/DRT.

**Properties:**
- `mode`: The transport mode.
- `time`: The current simulation time.
- `agent`: The `PersonAgent` making the choice.
- `destination`: The potential destination location.
- `sharingProviders`: Sharing providers the agent is a member of for the current `mode`.
- `drtProviders`: DRT providers the agent is a member of for the current `mode`.

**Functions:**
- `List<SharingProviderAgent>.checkOperatingHours()`: Filters providers currently in operation.
- `List<SharingProviderAgent>.stations()`: Flattens providers to their stations.
- `List<SharingStationAgent>.checkAgentInFootZones()`: Filters stations where the agent is in a designated foot zone.
- `List<SharingStationAgent>.checkAgentInRadius(radius, pedestrian, impedance)`: Filters stations within a certain radius.
- `List<SharingStationAgent>.checkDestinationInFootZones()`: Filters stations that have a return station near the destination.
- `List<SharingStationAgent>.checkDestinationInRadius(radius, pedestrian, impedance)`: Filters stations near destination by radius.
- `List<DrtProviderAgent>.checkOperatingHoursAndArea()`: Filters DRT providers that operate at the current time/area.

### ProviderRuleScope
Used within `.providerRule { ... }`.

**Properties:**
- All properties from `AgentRuleScope`.

**Functions:**
- `available(resources)` / `available(vararg resources)`: Marks the mode as available and lists the affected providers.
- `notAvailable()`: Marks the mode as unavailable.
- `modeAlreadyInUse()`: Returns true if the agent is already using a resource for this mode.
- `homeBasedVehicleRule(useProvider: Boolean)`: Implements standard logic for private vehicles (must be at home to start, or already using the vehicle).
- `List<Any>.checkAnyAvailable()`: Returns `available(this)` if the list is not empty, otherwise `notAvailable()`.

### ResourceRuleScope
Used within `.resourceRule { ... }`.

**Properties:**
- All properties from `AgentRuleScope`.
- `characteristics`: The full `ModeChoiceCharacteristics` object.
- `resourceUnavailable`: Returns `null`, indicating the resource is not available.

**Functions:**
- `availableWithoutResource()`: Returns a `NoResourceMode` (available but no specific vehicle tracked).
- `List<SharingStationAgent>.checkVehiclesAvailable()`: Filters stations with at least one vehicle.
- `selectStationByMinDistance(pedestrianMode, impedance)`: Selects the nearest station.
- `selectStationByMinTravelTime(pedestrianMode, impedance)`: Selects station with minimum travel time.
- `selectMinDistOneWaySharing(pedestrianMode, impedance)`: Finds the best start/end station pair for one-way trips.
- `selectRideOfferByMinDuration()`: Requests and picks the best DRT offer.

---

## Default Rules

The system provides pre-defined default rules for common transport modes in `DefaultRules.kt`.

### Car Availability (`defaultCarAvailability`)
- **Static:** Person must have a license and the household must own at least one car.
- **Provider:** Home-based. The household is the provider. Agent must be at home to start a new car tour.
- **Resource:** Selects the "best" car from the household (any available car but prefer car where the agent is marked as its main user).

```kotlin
fun AvailabilityByRuleBuilder.defaultCarAvailability(car: Mode) = availabilityOf(car).staticRule {
    person.hasLicense && person.household.cars.isNotEmpty()
}.providerRule {
    homeBasedVehicleRule(useProvider = true)
}.resourceRule {
    characteristics.person.getBestCarOrNull()?.let { vehicle ->
        CarResource(car, vehicle)
    }
}
```

### Bike Availability (`defaultBikeRule`)
- **Static:** Person must own a bike.
- **Provider:** Home-based. No provider locking required. Agent must be at home to start.
- **Resource:** Always available if provider check passes.

```kotlin
fun AvailabilityByRuleBuilder.defaultBikeRule(bike: Mode) = availabilityOf(bike).staticRule {
    person.hasBike
}.providerRule {
    homeBasedVehicleRule(useProvider = false)
}.resourceRule {
    BikeResource(bike)
}
```

### Station-Based Sharing (`defaultSharingStationRules`)
- **Static:** Person must be a member of the sharing service.
- **Provider:** Checks operating hours and if the agent is near a station.
- **Resource:** Selects the nearest station with at least one vehicle available.

```kotlin
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
```

### Free-Floating Sharing (`defaultSharingFloatingRules`)
- **Static:** Person must be a member of the sharing service.
- **Provider:** Checks operating hours. Both agent and destination must be within walking distance of a station/zone.
- **Resource:** Selects the best start and end zone combination based on minimum total travel distance.

```kotlin
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
```

### Ride Pooling / DRT (`defaultDrtRules`)
- **Static:** Person must be a member of the DRT service.
- **Provider:** Checks if any provider operates at the current time in the required area (agent location to destination).
- **Resource:** Requests offers from all valid providers and selects the one with the shortest total trip duration.

```kotlin
fun AvailabilityByRuleBuilder.defaultDrtRules(drt: Mode) = availabilityOf(drt).staticRule {
    person.drtMemberships.any { it.mode == mode }
}.providerRule {
    drtProviders.checkOperatingHoursAndArea()
        .checkAnyAvailable()
}.resourceRule {
    drtProviders.checkOperatingHoursAndArea()
        .selectRideOfferByMinDuration()
}
```
