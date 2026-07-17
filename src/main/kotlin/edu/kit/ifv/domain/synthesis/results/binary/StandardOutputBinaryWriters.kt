package edu.kit.ifv.domain.synthesis.results.binary

import edu.kit.ifv.binary.BinaryWriter
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Activity
import edu.kit.ifv.domain.synthesis.SynthesisHousehold
import edu.kit.ifv.domain.synthesis.SynthesisPerson
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.results.FixedDestinationElements
import edu.kit.ifv.utils.PathChecksum
import java.nio.file.Path

fun writeFixedDestinations(
    outputPath: Path,
    fixedDestinationWriter: BinaryWriter<FixedDestinationBinaryRecord>,
    fixedDestinations: List<FixedDestinationElements>
) {
    val mappedDestinations = fixedDestinations.map {
        FixedDestinationBinaryRecord(it.person.personId, it.activityType.code, it.location.zoneId.value)
    }
    val dest = outputPath.resolve("fixedDestinations.binary")
    fixedDestinationWriter.toBinary(dest, mappedDestinations, PathChecksum.from(0))
}

fun <S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> writeCarsBinary(
    outputPath: Path,
    carWriter: BinaryWriter<SynthesisCarBinaryRecord>,
    households:  List<SynthesisHousehold<S, T>>) {
    val unrolledCars = households.map { household ->
        household.cars.map { car ->
            SynthesisCarBinaryRecord(household.id, car.id.value)
        }
    }.flatten()
    val carPath = outputPath.resolve("cars.binary")
    carWriter.toBinary(carPath, unrolledCars, PathChecksum.from(0))
}

fun <S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>
        writePersonsBinary(outputPath: Path, personWriter: BinaryWriter<SynthesisPerson<S, T>>, people: List<SynthesisPerson<S, T>>) {
    val personPath = outputPath.resolve("persons.binary")
    personWriter.toBinary(personPath, people, PathChecksum.from(0))
}

fun <S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> writeHouseholdsBinary(
    outputPath: Path,
    householdWriter: BinaryWriter<SynthesisHousehold<S, T>>,
    households: List<SynthesisHousehold<S, T>>
) {
    val householdPath = outputPath.resolve("households.binary")
    householdWriter.toBinary(householdPath, households, PathChecksum.from(0))
}

fun writeActivitiesBinary(
    outputPath: Path,
    activityWriter: BinaryWriter<ActivitiesBinaryRecord>,
    activities: Map<SynthesisPerson<*, *>, Collection<Activity>>
) {
    val unrolledActivities = activities.map { (household, activity) ->
        val personID = household.personId
        activity.map { action ->
            ActivitiesBinaryRecord(
                personID,
                action.duration.inWholeMinutes,
                action.startTime.minutesSinceStart,
                action.type.code,
            )
        }
    }.flatten()
    val activityPath = outputPath.resolve("activities.binary")
    activityWriter.toBinary(activityPath, unrolledActivities, PathChecksum.from(0))
}