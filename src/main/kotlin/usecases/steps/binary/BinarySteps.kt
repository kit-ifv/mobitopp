package usecases.steps.binary

import usecases.steps.LoadBinaryStep
import usecases.steps.LoadPersonsContext
import usecases.steps.LoadPlannedActivitiesContext
import usecases.steps.WriteBinaryStep
import usecases.steps.legacyData.LoadHouseholdContext
import usecases.steps.legacyData.LoadPrivateCarsContext
import usecases.steps.legacyData.LoadZonesContext
import java.nio.file.Path

fun LoadPersonsContext.loadPersonsFromBinary(path: Path) {
    val converter = BinaryPersonReader(householdRepository.elements.associateBy { it.id }::getValue, simulationSeed)
    runStep {
        LoadBinaryStep(
            path,
            parser = converter,
            repository = personRepository,
            dependentRepositories = setOf(householdRepository)
        )
    }
}

fun LoadHouseholdContext.loadHouseholdFromBinary(path: Path) {
    runStep {
        val converter = BinaryHouseholdReader(zoneRepository.elements.associateBy { it.id }::getValue, simulationSeed)
        LoadBinaryStep(path, converter, householdRepository, setOf(zoneRepository))
    }
}

fun LoadZonesContext.loadZonesFromBinary(path: Path) {
    val converter = BinaryZoneReader(simulationSeed, areaTypeCodes)
    runStep {
        LoadBinaryStep(path, converter, zoneRepository, emptySet())
    }
}

fun LoadPrivateCarsContext.loadCarsFromBinary(path: Path) {
    val converter = BinaryCarReader(
        householdRepository.elements.associateBy { it.id }::getValue,
        personRepository.elements.associateBy { it.id }::getValue,
    ) {
        it.owner.location
    }
    runStep {
        LoadBinaryStep(path, converter, carRepository, emptySet())
    }
}

fun LoadPlannedActivitiesContext.loadActivitiesFromBinary(path: Path) {
    val converter = BinaryActivityReader(
        activityTypeCodes,
        { personRepository.getById(it) ?: throw NoSuchElementException("No person of id $it in personRepository") },
        simulationSeed
    )
    runStep {
        LoadBinaryStep(path, converter, plannedActivityRepository, setOf(personRepository))
    }
}


/* TODO There is no reason to require the LoadHouseholdContext or any other of the predefined context, but sadly writing
     a readonly interface also requires adding the interface to the underlying context, as the interfaces do not specify
     what they require. The correct procedure would be i.e. that LoadHouseholdContext is a : ReadonlyZonesContext,
     MutableHouseholdContext, etc. If that would be the case, this extension method could be built upon a readonly
     household context. Which would be better, because you can write a household repository to binary, even if your
     context does not fulfill LoadHouseholdContext because Zones are missing (or sth else)
 */

fun LoadHouseholdContext.writeHouseholdBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryHouseholdWriter(), householdRepository)
    }
}

fun LoadZonesContext.writeZonesBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryZoneWriter(), zoneRepository)
    }
}

fun LoadPersonsContext.writePersonsBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryPersonWriter(), personRepository)
    }
}

fun LoadPlannedActivitiesContext.writeActivitiesBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryActivityWriter(), plannedActivityRepository)
    }
}

fun LoadPrivateCarsContext.writeCarsBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryCarWriter(), carRepository)
    }
}
