package application.steps.parser

import application.steps.parser.csv.LoadHouseholdContext
import application.steps.parser.csv.LoadPersonsContext
import application.steps.parser.csv.LoadPlannedActivitiesContext
import application.steps.parser.csv.LoadPrivateCarsContext
import application.steps.parser.csv.LoadZonesContext
import core.modelsteps.binary.LoadBinaryStep
import core.modelsteps.binary.WriteBinaryStep
import domain.synthesis.parser.binary.BinaryActivityReader
import domain.synthesis.parser.binary.BinaryActivityWriter
import domain.synthesis.parser.binary.BinaryCarReader
import domain.synthesis.parser.binary.BinaryCarWriter
import domain.synthesis.parser.binary.BinaryHouseholdReader
import domain.synthesis.parser.binary.BinaryHouseholdWriter
import domain.synthesis.parser.binary.BinaryPersonReader
import domain.synthesis.parser.binary.BinaryPersonWriter
import domain.synthesis.parser.binary.BinaryZoneReader
import domain.synthesis.parser.binary.BinaryZoneWriter
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
    val converter = BinaryZoneReader(simulationSeed, regionTypeCodes)
    runStep {
        LoadBinaryStep(path, converter, zoneRepository, emptySet())
    }
}

fun LoadPrivateCarsContext.loadCarsFromBinary(path: Path) {
    val converter = BinaryCarReader(
        householdRepository.elements.associateBy { it.id }::getValue,
        personRepository.elements.associateBy { it.id }::getValue,
    )

    runStep {
        LoadBinaryStep(path, converter, carRepository, emptySet())
    }
}

fun LoadPlannedActivitiesContext.loadActivitiesFromBinary(path: Path) {
    val converter = BinaryActivityReader(
        activityTypes,
        { personRepository.find(it) ?: throw NoSuchElementException("No person of id $it in personRepository") },
        simulationSeed
    )
    runStep {
        LoadBinaryStep(path, converter, plannedActivityRepository, setOf(personRepository))
    }
}

/* TODO There is no reason to require the LoadHouseholdContext or any other of the predefined mobitopp, but sadly writing
     a readonly interface also requires adding the interface to the underlying mobitopp, as the interfaces do not specify
     what they require. The correct procedure would be i.e. that LoadHouseholdContext is a : ReadonlyZonesContext,
     MutableHouseholdContext, etc. If that would be the case, this extension method could be built upon a readonly
     household mobitopp. Which would be better, because you can write a household repository to binary, even if your
     mobitopp does not fulfill LoadHouseholdContext because Zones are missing (or sth else)
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
