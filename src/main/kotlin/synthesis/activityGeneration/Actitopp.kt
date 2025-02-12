package synthesis.activityGeneration

import SynthesisSteps
import datastructure.Activity
import datastructure.RawActivity
import domain.enums.LegacyActivityType
import domain.location.LOCATIONUNKNOWN
import edu.kit.ifv.mobitopp.actitopp.ActiToppHousehold
import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.ActivityType
import edu.kit.ifv.mobitopp.actitopp.HActivity
import edu.kit.ifv.mobitopp.actitopp.InvalidPatternException
import edu.kit.ifv.mobitopp.actitopp.ModelFileBase
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import synthesis.CSVOutput
import synthesis.RawSurveyInfo
import synthesis.SurveyInfo
import synthesis.domain.SynthesisHousehold
import synthesis.domain.SynthesisPerson
import synthesis.employment
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
        employment.code,
        sex.code

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
        employment.code,
        sex.code,
        -1
    )
}

fun SynthesisHousehold<out SurveyInfo>.toActiToppHousehold(): ActiToppHousehold {
    val hh = ActiToppHousehold(
        id,
        numberOfChilds,
        numberOfYouths,
        location.requireZone().regionType.code,
        amountOfCars
    )
    return hh
}

@Suppress("MagicNumber") // TODO this may be relevant to fix, age 10 is magic
val SynthesisHousehold<out SurveyInfo>.numberOfChilds get() = members.count { it.age <= 10 }

@Suppress("MagicNumber") // TODO this may be relevant to fix, age 10 is magic
val SynthesisHousehold<out SurveyInfo>.numberOfYouths get() = members.count { it.age in 10..<18 }

@Suppress("MagicNumber") // 1234 is the default number from actitopp example
fun SynthesisSteps<RawSurveyInfo>.generateActivitiesViaActitopp() {
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

fun HActivity.toReengineeredActivity(): Activity {
    return RawActivity(
        location = LOCATIONUNKNOWN,
        startTime = startTime.toDuration(DurationUnit.MINUTES).sinceStart,
        endTime = endTime.toDuration(DurationUnit.MINUTES).sinceStart,
        type = activityType.toReengineeredType()
    )
}

fun ActivityType.toReengineeredType(): domain.enums.ActivityType {
    return when (typeasChar) {
        'W' -> LegacyActivityType.WORK
        'E' -> LegacyActivityType.EDUCATION
        'L' -> LegacyActivityType.LEISURE
        'S' -> LegacyActivityType.SHOPPING
        'T' -> LegacyActivityType.LEISURE_TRAVEL // TODO verify what TRANSPORT SHOULD BE
        'H' -> LegacyActivityType.HOME
        'x' -> LegacyActivityType.UNDEFINED
        else -> LegacyActivityType.UNDEFINED
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
