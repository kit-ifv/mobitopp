package synthesis.activityGeneration

import datastructure.Activity
import domain.enums.ActivityType
import domain.location.LOCATIONUNKNOWN
import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.InvalidPatternException
import edu.kit.ifv.mobitopp.actitopp.ModelFileBase
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import synthesis.SurveyInfo
import synthesis.domain.SynthesisPerson
import synthesis.employment
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import utils.Decodable
import utils.units.AbsoluteTime
import utils.units.sinceStart
import kotlin.time.Duration
@Suppress(
    "MagicNumber"
) // 1234 is just a random seed, I took this from actitopp example; there is no thought behind this number
class ActitoppGenerator(
    val fileBase: ModelFileBase = ModelFileBase(),
    val randomgenerator: RNGHelper = RNGHelper(1235),
    val purposes: ChoiceModelPurposes,
) : GenerateActivitySchedule<SurveyInfo> {
    override fun generate(person: SynthesisPerson<out SurveyInfo>): PreliminaryActivitySchedule {
        val actitoppPerson = convertToSingularHousehold(person)
        // Error handling
        while(true) {
            var counter = 0
            try {
                actitoppPerson.generateSchedule(fileBase, randomgenerator)
                break
            } catch(why: InvalidPatternException) {
                println("Actitopp throws an exception for ${actitoppPerson.persIndex} Iteration $counter")
                counter++
            }
        }


        require(actitoppPerson.weekPattern.allActivities.isNotEmpty()) {
            "Somehow a person managed to be created without activities [${actitoppPerson.persIndex}]"
        }

        return PreliminaryActivitySchedule(
            actitoppPerson.weekPattern.allActivities.map { it.toReengineeredActivity(purposes) }.toMutableList()
        )
    }

    /* Actitopp breaks when using joint actions. The workaround is to imitate each person to be part of a fake "household"
       which only contains this person, but all other attributes are taken as is from the original household.
     */
    private fun convertToSingularHousehold(person: SynthesisPerson<out SurveyInfo>): ActitoppPerson {
        val actiToppHousehold = person.household.toActiToppHousehold()
        return person.run {
            ActitoppPerson(
                actiToppHousehold,
                1,
                personId,
                age,
                employment.code,
                sex.code,
            )
        }
    }
}

data class PreliminaryActivitySchedule(private val activities: MutableList<Activity>) :
    MutableList<Activity> by activities {

    companion object {

        val STAY_AT_HOME = PreliminaryActivitySchedule(mutableListOf())

        operator fun invoke(
            decoder: Decodable<ActivityType>,
            lambda: ScheduleBuilder.() -> Unit
        ): PreliminaryActivitySchedule {
            val builder = ScheduleBuilder(decoder)
            builder.lambda()
            return builder.build()
        }
    }

    override fun toString(): String {
        return activities.toString()
    }

    class ScheduleBuilder(private val decoder: Decodable<ActivityType>) {
        val activities: MutableList<Activity> = mutableListOf()

        fun home(start: Duration, end: Duration) {
            extracted(start, end, "HOME")
        }

        fun work(start: Duration, end: Duration) {
            extracted(start, end, "WORK")
        }

        fun education(start: Duration, end: Duration) {
            extracted(start, end, "EDUCATION")
        }

        fun shopping(start: Duration, end: Duration) {
            extracted(start, end, "SHOPPING")
        }

        fun leisure(start: Duration, end: Duration) {
            extracted(start, end, "LEISURE")
        }

        private fun extracted(start: Duration, end: Duration, type: String) {
            activities.add(Activity.fromTimes(start, end, decoder.decode(type)))
        }

        fun build(): PreliminaryActivitySchedule {
            return PreliminaryActivitySchedule(activities)
        }
    }
}

fun Activity.Companion.fromTimes(start: AbsoluteTime, end: AbsoluteTime, type: ActivityType): Activity {
    require(
        start <= end
    ) { "Cannot create activity where start time is larger than end time: [start=$start , end=$end]" }

    return fromDuration(LOCATIONUNKNOWN, start, end - start, type)
}

fun Activity.Companion.fromTimes(start: Duration, end: Duration, type: ActivityType): Activity {
    return fromTimes(start.sinceStart, end.sinceStart, type)
}
