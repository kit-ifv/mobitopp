# Model Step Implementations

This document provides a comprehensive overview of the model steps and helper functions available in the `application.steps.model` and `application.steps.parser` packages.

## Package `application.steps.model`

This package contains model steps that represent specific simulation modules or data manipulation tasks.

### `AddDrtMemberships.kt`

*   **`addDrtMembershipsIf(predicate)`**
    *   **Intent**: Adds DRT memberships to persons based on a predicate.
    *   **Scope**: Must be called within a `MutableRepository<MutablePerson, PersonId>` scope.
    *   **Parameters**:
        *   `predicate`: A lambda evaluated for each person and DRT provider to determine membership.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasDrtProviderRepo`.
        *   Config `CFG` must implement `Config`.

### `AddDrtProvider.kt`

*   **`newDrtProvider(idProvider, scope)`**
    *   **Intent**: Creates and adds a new DRT provider to the repository.
    *   **Scope**: Must be called within a `MutableRepository<MutableDrtProviderData, DrtProviderId>` scope.
    *   **Parameters**:
        *   `idProvider`: Provides the ID for the new provider (defaults to `GlobalDrtProviderIdCounter`).
        *   `scope`: Lambda to configure the new provider.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `Context`.

*   **`simpleDrtAlgorithm(impedance, serviceArea, ...)`**
    *   **Intent**: Helper function to configure a `SimpleMatrixDrtAlgorithm`.
    *   **Parameters**:
        *   `impedance`: The impedance model.
        *   `serviceArea`: Collection of zones where the service is available.
        *   `numVehicles`: Fleet size (defaults to service area size).
        *   `avgWaitingTime`: Average wait time (defaults to 4 minutes).
        *   `operationHours`: Start and end hour (defaults to 0-24).

### `AssignCarUser.kt`

*   **`assignMainCarUsers(model)`**
    *   **Intent**: Assigns main car users to private cars.
    *   **Scope**: Must be called within a `MutableRepository<MutablePrivateCar, CarId>` scope.
    *   **Parameters**:
        *   `model`: The model used for assignment (defaults to `AssignCarUserModel()`).
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasPersonRepo`.

### `BuildAgentsStep.kt`

*   **`buildSimulationAgents(personStateMachine, durationRandomizer, ...)`**
    *   **Intent**: Converts person and DRT data into simulation agents.
    *   **Parameters**:
        *   `personStateMachine`: Factory for person agents.
        *   `durationRandomizer`: Strategy for randomizing activity durations (defaults to `NoDurationRandomizer`).
        *   `drtStateMachine`: Optional factory for DRT provider agents.
        *   `drtAlgorithm`: Optional mapping from DRT provider to algorithm.
    *   **Context/Config Requirements**:
        *   Context `C` must implement several `Has...Repo` interfaces for persons, households, cars, and agents.
        *   Config `CFG` must implement `SimulationConfig`.

### `HomeLocationStep.kt`

*   **`assignHouseholdLocation(model)`**
    *   **Intent**: Assigns home locations to each household individually.
    *   **Scope**: Must be called within a `MutableRepository<MutableHousehold, HouseholdId>` scope.
    *   **Parameters**:
        *   `model`: Location generation model (defaults to `AssignAroundZoneCentroid(100.meters)`).
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasZoneRepo`.

*   **`assignHouseholdLocationsInBulk(model)`**
    *   **Intent**: Assigns home locations to households in bulk, grouped by zone.
    *   **Scope**: Must be called within a `MutableRepository<MutableHousehold, HouseholdId>` scope.
    *   **Parameters**:
        *   `model`: Bulk location generation model (defaults to `TrivialGroupStrategy`).
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasZoneRepo`.

### `LoadBehaviorModelsStep.kt`

*   **`loadBehaviorModels(...)`**
    *   **Intent**: Initializes choice models for the simulation.
    *   **Parameters**: Various choice models and characteristics implementations.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasMutableBehaviorModel`.

### `SimulatePlannedActivities.kt`

*   **`simulate(simulator)`**
    *   **Intent**: Runs the simulation for all person agents.
    *   **Parameters**:
        *   `simulator`: Function providing the `Simulator` (defaults to `parallel`).
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasPersonAgentRepo`.
        *   Config `CFG` must implement `SimulationConfig`.

---

## Package `application.steps.parser`

This package contains steps for loading data from various formats (binary, Visum, custom).

### `LoadBinarySteps.kt`

This file provides functions to load and write simulation entities in a fast binary format.

*   **`loadPersonsFromBinary(path)`**
    *   **Intent**: Loads persons from a binary file into the current repository.
    *   **Scope**: Must be called within a `MutableRepository<MutablePerson, PersonId>` scope.
    *   **Parameters**:
        *   `path`: The path to the binary file.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasHouseholdRepo<MutableHousehold, *>`, `HasSharingProviderRepo<*, SharingProvider>`, and `HasDrtProviderRepo<*, DrtProvider>`.
        *   Config must implement `ShortTermConfig`.

*   **`loadHouseholdFromBinary(path)`**
    *   **Intent**: Loads households from a binary file into the current repository.
    *   **Scope**: Must be called within a `MutableRepository<MutableHousehold, HouseholdId>` scope.
    *   **Parameters**:
        *   `path`: The path to the binary file.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasZoneRepo<*, Zone>`.
        *   Config must implement `ShortTermConfig`.

*   **`loadZonesFromBinary(path)`**
    *   **Intent**: Loads zones from a binary file into the current repository.
    *   **Scope**: Must be called within a `MutableRepository<MutableZone, ZoneId>` scope.
    *   **Parameters**:
        *   `path`: The path to the binary file.
    *   **Context/Config Requirements**:
        *   Context must implement `Context`.
        *   Config `CFG` must implement `RegionCodesConfig` and `SourceFilesConfig`.

*   **`loadCarsFromBinary(path)`**
    *   **Intent**: Loads private cars from a binary file into the current repository.
    *   **Scope**: Must be called within a `MutableRepository<MutablePrivateCar, CarId>` scope.
    *   **Parameters**:
        *   `path`: The path to the binary file.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasHouseholdRepo<MutableHousehold, *>` and `HasPersonRepo<*, Person>`.

*   **`loadActivitiesFromBinary(path)`**
    *   **Intent**: Loads planned activities from a binary file into the current repository.
    *   **Scope**: Must be called within a `MutableRepository<MutablePlannedActivity, ActivityId>` scope.
    *   **Parameters**:
        *   `path`: The path to the binary file.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasPersonRepo<MutablePerson, *>`.
        *   Config `CFG` must implement `ShortTermConfig` and `ActivityTypesConfig`.

*   **`writeHouseholdBinary(path)`**
    *   **Intent**: Writes households from the repository to a binary file.
    *   **Parameters**:
        *   `path`: Output file path.
    *   **Context/Config Requirements**:
        *   Receiver must implement `HasHouseholdRepo<*, Household>`.

*   **`writeZoneBinary(path)`**
    *   **Intent**: Writes zones from the repository to a binary file.
    *   **Parameters**:
        *   `path`: Output file path.
    *   **Context/Config Requirements**:
        *   Receiver must implement `HasZoneRepo<*, Zone>`.

*   **`writePersonBinary(path)`**
    *   **Intent**: Writes persons from the repository to a binary file.
    *   **Parameters**:
        *   `path`: Output file path.
    *   **Context/Config Requirements**:
        *   Receiver must implement `HasPersonRepo<*, Person>`.

*   **`writeActivitiesBinary(path)`**
    *   **Intent**: Writes activities of all persons in the repository to a binary file.
    *   **Parameters**:
        *   `path`: Output file path.
    *   **Context/Config Requirements**:
        *   Receiver must implement `HasPersonRepo<*, Person>`.

*   **`writeCarsBinary(path)`**
    *   **Intent**: Writes private cars from the repository to a binary file.
    *   **Parameters**:
        *   `path`: Output file path.
    *   **Context/Config Requirements**:
        *   Receiver must implement `HasCarRepo<*, PrivateCar>`.

### `LoadImpedance.kt`

*   **`loadImpedance()`**
    *   **Intent**: Loads travel time, cost, and distance matrices from configured paths.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasModes` and `HasMutableImpedance`.
        *   Config `CFG` must implement `MatrixConfig` and `UnitConfig`.

*   **`loadTeleportation()`**
    *   **Intent**: Assigns a `Teleportation` impedance model to the context.
    *   **Description**: The `Teleportation` model provides:
        *   Zero travel costs for all trips.
        *   A minimal fixed duration (1 second).
        *   A minimal fixed distance (1 meter).
    *   **Context/Config Requirements**:
        *   Receiver must implement `HasMutableImpedance`.

### `LoadRoadNetworkStep.kt`

*   **`loadVisumNetwork(file, localeLambda)`**
    *   **Intent**: Loads a road network from a Visum `.net` file.
    *   **Parameters**:
        *   `file`: Path to the `.net` file.
        *   `localeLambda`: Lambda to configure the Visum locale.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasMutableRoadNetwork`.

---

## Package `application.steps.parser.csv`

This package provides high-level steps for loading data from CSV files, including repository scopes and parser configuration.

### `LoadHouseholds.kt`

*   **`households(sealed, scope)`**
    *   **Intent**: Provides a scope for configuring the household repository.
    *   **Parameters**:
        *   `sealed`: Whether the repository should be sealed after the scope finishes (defaults to `false`).
        *   `scope`: The configuration scope.
    *   **Context/Config Requirements**:
        *   Context `CTXT` must implement `HasHouseholdRepo`.
*   **`loadHouseholds(resource)`**
    *   **Intent**: Model step to add households from a resource (CSV or binary) to the current repository.
    *   **Scope**: Must be called within a `MutableRepository<MutableHousehold, HouseholdId>` scope.
    *   **Parameters**:
        *   `resource`: The resource to load from.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `Context`.
*   **`householdCsv(parser, path, delimiter, binaryCache)`**
    *   **Intent**: Creates a `Resource<MutableHousehold>` for CSV loading, optionally with binary caching.
    *   **Parameters**:
        *   `parser`: CSV parser (defaults to `householdCsvParser()`).
        *   `path`: File path (defaults to `config.sourceFiles.householdCSV`).
        *   `delimiter`: CSV delimiter (defaults to `config.sourceFiles.defaultCsvDelimiter`).
        *   `binaryCache`: Optional binary cache config (defaults to `binaryHouseholdFormat()`).
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasZoneRepo<*, Zone>`.
        *   Config `CFG` must implement `SourceFilesConfig`, `UnitConfig`, and `HouseholdCodesConfig`.
*   **`householdCsvParser(customizeCsvConfig)`**
    *   **Intent**: Creates a standard CSV parser for households.
    *   **Parameters**:
        *   `customizeCsvConfig`: Lambda to customize the parser configuration.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasZoneRepo<*, Zone>`.
        *   Config `CFG` must implement `SourceFilesConfig`, `UnitConfig`, and `HouseholdCodesConfig`.
*   **`filterHouseholds(valid)`**
    *   **Intent**: Filters the current household repository to keep only those with IDs in the provided collection.
    *   **Scope**: Must be called within a `MutableRepository<MutableHousehold, HouseholdId>` scope.
*   **`filterFractionOfPopulation(fraction)`**
    *   **Intent**: Filters a fraction of the population from the household repository.
    *   **Scope**: Must be called within a `MutableRepository<MutableHousehold, HouseholdId>` scope.
    *   **Parameters**:
        *   `fraction`: Fraction of households to keep (defaults to `config.fractionOfPopulation`).
    *   **Context/Config Requirements**:
        *   Config `CFG` must implement `SimulationConfig`.

### `LoadPersons.kt`

*   **`persons(sealed, scope)`**
    *   **Intent**: Provides a scope for configuring the person repository.
    *   **Parameters**:
        *   `sealed`: Whether the repository should be sealed after (defaults to `false`).
        *   `scope`: The configuration scope.
    *   **Context/Config Requirements**:
        *   Context `CTXT` must implement `HasPersonRepo`.
*   **`loadPersons(resource, dependentRepositories)`**
    *   **Intent**: Model step to add persons from a resource.
    *   **Scope**: Must be called within a `MutableRepository<MutablePerson, PersonId>` scope.
    *   **Parameters**:
        *   `resource`: The resource to load from.
        *   `dependentRepositories`: Repositories that must be loaded first (defaults to household, sharing, and DRT repos).
    *   **Context/Config Requirements**:
        *   Context `C` must implement `Context`.
*   **`personCsv(parser, path, delimiter, binaryCache)`**
    *   **Intent**: Creates a `Resource<MutablePerson>` for CSV loading.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasHouseholdRepo`, `HasSharingProviderRepo`, and `HasDrtProviderRepo`.
        *   Config `CFG` must implement `SourceFilesConfig`, `UnitConfig`, and `PersonCodesConfig`.

### `LoadZones.kt`

*   **`zones(sealed, scope)`**
    *   **Intent**: Provides a scope for configuring the zone repository.
    *   **Context/Config Requirements**:
        *   Context `CTXT` must implement `HasZoneRepo` for `MutableZone`.
*   **`loadZones(resource)`**
    *   **Intent**: Model step to add zones from a resource.
    *   **Scope**: Must be called within a `MutableRepository<MutableZone, ZoneId>` scope.
*   **`zoneCsv(parser, path, delimiter, binaryCache)`**
    *   **Intent**: Creates a `Resource<MutableZone>` for CSV loading.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `Context`.
        *   Config `CFG` must implement `SourceFilesConfig`, `UnitConfig`, and `RegionCodesConfig`.

### `LoadPrivateCars.kt`

*   **`cars(sealed, scope)`**
    *   **Intent**: Provides a scope for configuring the private car repository.
    *   **Context/Config Requirements**:
        *   Context `CTXT` must implement `HasPrivateCarRepo`.
*   **`loadCars(resource)`**
    *   **Intent**: Model step to add cars from a resource.
    *   **Scope**: Must be called within a `MutableRepository<MutablePrivateCar, CarId>` scope.
*   **`carCsv(parser, path, delimiter, binaryCache)`**
    *   **Intent**: Creates a `Resource<MutablePrivateCar>` for CSV loading.
    *   **Context/Config Requirements**:
        *   Config `CFG` must implement `SourceFilesConfig`, `UnitConfig`, and `CarCodesConfig`.

### `LoadPlannedActivities.kt`

*   **`plannedActivities(scope)`**
    *   **Intent**: Provides a scope for configuring the planned activities repository.
    *   **Context/Config Requirements**:
        *   Context `CTXT` must implement `HasPlannedActivities`.
*   **`loadActivities(resource)`**
    *   **Intent**: Model step to add activities from a resource.
    *   **Scope**: Must be called within a `MutableRepository<MutablePlannedActivity, ActivityId>` scope.
*   **`plannedActivityCsv(parser, path, delimiter, binaryCache)`**
    *   **Intent**: Creates a `Resource<MutablePlannedActivity>` for CSV loading.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasPersonRepo` and `HasZoneRepo`.
        *   Config `CFG` must implement `SourceFilesConfig`, `UnitConfig`, and `PurposesConfig`.

### `LoadDrtProviders.kt`

*   **`drtProviders(sealed, scope)`**
    *   **Intent**: Provides a scope for configuring DRT provider repositories.
    *   **Context/Config Requirements**:
        *   Context `CTXT` must implement `HasZoneRepo` and `HasDrtProviderRepo`.
*   **`loadDrtProviders(resource, dependentRepositories)`**
    *   **Intent**: Model step to add DRT providers from a resource.
    *   **Scope**: Must be called within a `MutableRepository<MutableDrtProviderData, DrtProviderId>` scope.
    *   **Parameters**:
        *   `dependentRepositories`: Defaults to `zoneRepository`.
*   **`ridePoolingProviderCsv(path, parser, delimiter, binaryCache)`**
    *   **Intent**: Creates a CSV resource for ride-pooling providers.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasZoneRepo`.
        *   Config `CFG` must implement `DrtSourceFilesConfig`, `SourceFilesConfig`, and `DrtModesConfig`.

### `LoadSharingStations.kt`

*   **`sharingProviders(sealed, scope)`**
    *   **Intent**: Provides a scope for configuring sharing provider repositories.
    *   **Context/Config Requirements**:
        *   Context `CTXT` must implement `HasSharingProviderRepo`.
*   **`loadSharingProviders(resource, dependentRepositories)`**
    *   **Intent**: Model step to add sharing providers.
    *   **Scope**: Must be called within a `MutableRepository<MutableSharingProvider, SharingProviderId>` scope.
    *   **Parameters**:
        *   `dependentRepositories`: Defaults to `zoneRepository`.
*   **`bikeSharingProviderCsv()`, `carSharingStationProviderCsv()`, `carSharingFloatingProviderCsv()`**
    *   **Intent**: Create CSV resources for specific sharing types.
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasZoneRepo`.
        *   Config `CFG` must implement `SharingSourceFilesConfig`, `SourceFilesConfig`, and `SharingModesConfig`.

### `LoadAttractivenessStep.kt`

*   **`loadAttractivenessModelFromCsv(path, work, privateVisit, activityTypes)`**
    *   **Intent**: Loads zone attractiveness for different activity types from a CSV file.
    *   **Parameters**:
        *   `path`: Defaults to `config.attractivenessFile`.
        *   `work`: The work activity type (defaults to `config.work`).
        *   `privateVisit`: The private visit activity type (defaults to `config.privateVisit`).
        *   `activityTypes`: Set of activity types (defaults to all config activity types).
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasMutableAttractivenessModel`.
        *   Config `CFG` must implement `ActivityTypesConfig`, `AttractivenessFileConfig`, and `PurposesConfig`.

### `LoadFixedDestinations.kt`

*   **`fixedDestinations(homeActivity, resource)`**
    *   **Intent**: Loads and assigns fixed destinations (e.g., home locations) for activities.
    *   **Parameters**:
        *   `homeActivity`: The activity type representing home.
        *   `resource`: The resource to load from (defaults to `fixedDestinationCsv()`).
    *   **Context/Config Requirements**:
        *   Context `C` must implement `HasPlannedActivities` and `HasPersonRepo`.
        *   Config `CFG` must implement `SourceFilesConfig`, `UnitConfig`, and `PurposesConfig`.

### `LoadCustomPersons.kt`

*   **`customPersons(personClass, sealed, scope)`**
    *   **Intent**: Generic scope for configuring custom person repositories.
    *   **Parameters**:
        *   `personClass`: The class of the mutable person type.
    *   **Context/Config Requirements**:
        *   Context `CTXT` must implement `HasPersonRepo` and `Context`.
*   **`loadCustomPersons(resource, dependentRepositories)`**
    *   **Intent**: Generic model step to load custom person types.
    *   **Scope**: Must be called within a `MutableRepository<M, PersonId>` scope.
