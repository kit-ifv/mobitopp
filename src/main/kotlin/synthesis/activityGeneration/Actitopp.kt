package synthesis.activityGeneration

import SynthesisSteps
import datastructure.Activity
import datastructure.RawActivity
import domain.location.LOCATIONUNKNOWN
import edu.kit.ifv.mobitopp.actitopp.ActiToppHousehold
import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.ActivityType
import edu.kit.ifv.mobitopp.actitopp.HActivity
import edu.kit.ifv.mobitopp.actitopp.InvalidPatternException
import edu.kit.ifv.mobitopp.actitopp.ModelFileBase
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import synthesis.CSVOutput
import synthesis.SurveyInfo
import synthesis.domain.SynthesisHousehold
import synthesis.domain.SynthesisPerson
import synthesis.employment
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import usecases.steps.toCSV
import utils.units.sinceStart
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun SynthesisPerson<out SurveyInfo>.toActitoppPerson(household: ActiToppHousehold): ActitoppPerson {
    val number = household.householdmembers.size + 1
    val person = ActitoppPerson(
        household,
        number,
        personId,
        age,
        employment.encode(),
        sex.encode()

    )
    household.addHouseholdmember(person, number)

    return person
}

fun SynthesisPerson<out SurveyInfo>.toActitoppPerson(): ActitoppPerson {
    return ActitoppPerson(
        personId,
        0,
        0,
        age,
        employment.encode(),
        sex.encode(),
        -1
    )
}

fun SynthesisHousehold<out SurveyInfo>.toActiToppHousehold(): ActiToppHousehold {
    val hh = ActiToppHousehold(
        id,
        numberOfChilds,
        numberOfYouths,
        location.requireZone().regionType.encode(),
        amountOfCars
    )
    return hh
}

@Suppress("MagicNumber") // TODO this may be relevant to fix, age 10 is magic
val SynthesisHousehold<out SurveyInfo>.numberOfChilds get() = members.count { it.age <= 10 }

@Suppress("MagicNumber") // TODO this may be relevant to fix, age 10 is magic
val SynthesisHousehold<out SurveyInfo>.numberOfYouths get() = members.count { it.age in 10..<18 }

@Suppress("MagicNumber") // 1234 is the default number from actitopp example
fun <AREA> SynthesisSteps<AREA, out SurveyInfo>.generateActivitiesViaActitopp() {
    val fileBase = ModelFileBase()
    val randomgenerator = RNGHelper(1234)
    val schedules = people.map {
        val person = it.toActitoppPerson()
        person.generateScheduleBruteForce(fileBase, randomgenerator)
        person.weekPattern.allActivities
    }
    HActivityOutput.writeCSVToFile(outputDirectory.resolve("activity.csv"), schedules.flatten())
}

fun ActitoppPerson.generateScheduleBruteForce(fileBase: ModelFileBase, rngGen: RNGHelper) {
    while (true) {
        try {
            generateSchedule(fileBase, rngGen)
            return
        } catch (e: InvalidPatternException) {
            System.err.println(e.reason)
            System.err.println("person involved: $persIndex")
        }
    }
}

fun HActivity.toReengineeredActivity(purposes: ChoiceModelPurposes): Activity {
    return RawActivity(
        location = LOCATIONUNKNOWN,
        startTime = startTime.toDuration(DurationUnit.MINUTES).sinceStart,
        endTime = endTime.toDuration(DurationUnit.MINUTES).sinceStart,
        type = activityType.toReengineeredType(purposes)
    )
}

// TODO we should talk about activity types hierarchies and check how general types like
// EDUCATION are translated into more specific types like EDUCATION_PRIMARY in legacy mobiTopp
fun ActivityType.toReengineeredType(purposes: ChoiceModelPurposes): domain.enums.ActivityType {
    return when (typeasChar) {
        'W' -> purposes.work
        'E' -> purposes.education
        'L' -> purposes.leisure
        'S' -> purposes.shopping
        'T' -> purposes.leisureTravel // TODO verify what TRANSPORT SHOULD BE
        'H' -> purposes.home
        'x' -> purposes.undefined
        else -> purposes.undefined
    }
}

object HActivityOutput : CSVOutput<HActivity> {
    override val header: List<String> = listOf(
        "personId",
        "activityType",
        "observedTripDuration",
        "startTime",
        "duration",
        "tournr",
        "isMainActivity",
        "isSupertour"
    )

    override fun convert(element: HActivity): String {
        return element.run {
            toCSV(
                person.persIndex,
                activityType,
                "TODO observedTripDuration",
                startTime,
                duration,

                // TODO Tour index,
                isMainActivityoftheDay,
                "TODO isSupertour"
            )
        }
    }
}
