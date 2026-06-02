package domain.synthesis

import AssignmentStep
import HouseholdAssignmentStep
import domain.shared.behavior.AttractivenessModel
import domain.shared.datastructure.schedule.Activity
import domain.synthesis.attributes.household.HasMutableEconomicStatus
import domain.synthesis.attributes.household.HasMutableNumberOfCars
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.activityGeneration.GenerateHouseholdActivitySchedule
import domain.synthesis.behavior.cars.SynthesisCar
import domain.synthesis.behavior.cars.generation.GenerateCars
import domain.synthesis.behavior.cars.ownership.AssignMainUser
import domain.synthesis.behavior.economicstatus.DetermineEconomicStatus
import domain.synthesis.behavior.fixedDestinations.AssignFixedDestinationBuilder
import domain.synthesis.behavior.householdlocation.AssignHouseholdLocations
import domain.synthesis.behavior.householdlocation.GroupAssignHouseholdLocations
import domain.synthesis.behavior.sharingmemberships.SharingMembershipsBuilder
import domain.synthesis.results.FixedDestinationElements
import domain.synthesis.results.OpportunityOutput
import domain.synthesis.results.fastcsv.OutputWriters
import domain.synthesis.results.fastcsv.write
import domain.synthesis.results.fastcsv.writeActivities
import domain.synthesis.results.fastcsv.writeCars
import domain.synthesis.results.fastcsv.writeFixedDestinations
import domain.synthesis.results.fastcsv.writeOpportunities
import edu.kit.ifv.populationsynthesis.synthesis.CompletePopulationSynthesis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.collections.addProgressBar
import utils.collections.standardProgressBar
import java.nio.file.Path
import kotlin.random.Random

class SynthesisSteps<AREA, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
    val zones: List<AREA>,
    val surveyHouseholds: Collection<ISurveyHousehold<S, T>>,
    val attractivenessModel: AttractivenessModel,
    val outputDirectory: Path,
    val opportunities: List<OpportunityOutput>,
) {

    lateinit var householdsByZone: Map<AREA, List<SynthesisHousehold<S, T>>>

    @Deprecated("Be mindfull when using this getter in a hot loop")
    val households: List<SynthesisHousehold<S, T>> get() = householdsByZone.flatMap { it.value }

    @Deprecated("Be mindfull when using this getter in a hot loop")
    val people get() = households.flatMap { it.members }
    val activities: MutableMap<SynthesisPerson<*, *>, Collection<Activity>> = mutableMapOf()
    var cars = listOf<SynthesisCar>()
    var fixedDestinations: List<FixedDestinationElements> = emptyList()

    /**
     * Within the scope of this step, the fixed destinations for the agents are generated. The structure of the assign
     * strategy is created in the [AssignFixedDestinationBuilder] class, which provides some convenience methods for
     * frequently assigned fixed destinations.
     */
    fun assignFixedDestinations(lambda: AssignFixedDestinationBuilder<AREA, S, T>.() -> Unit) {
        val fixedDestinationBuilder = AssignFixedDestinationBuilder<AREA, S, T>(attractivenessModel)
        fixedDestinationBuilder.apply(lambda)

        val allFixedDestinations = fixedDestinationBuilder.steps.flatMap { it.generateFixedDestinations(households) }
        allFixedDestinations.addProgressBar(
            "Assign Fixed Destinations",
        ).forEach { it.person.fixedDestinations[it.activityType] = it.location }
        fixedDestinations = allFixedDestinations
    }

    fun assignSharingMemberships(lambda: SharingMembershipsBuilder<S, T>.() -> Unit) {
        val builder = SharingMembershipsBuilder<S, T>().apply(lambda)
        val steps = builder.build()
        households.addProgressBar("assign sharing memberships").forEach { hh ->
            hh.members.forEach {
                context(Random(it.personId)) {
                    val membership = steps.mapValues { (_, step) ->

                        step.assign(it)
                    }
                    membership.forEach { providerName, accepted ->
                        if (accepted) it.addMembership(providerName)
                    }
                }
            }
        }
    }

    // TODO refactor, use or discard this method
    fun assignLocationsForAll(lambda: () -> GroupAssignHouseholdLocations<in AREA, SynthesisHousehold<S, T>>) {
        val strategy = lambda()

        householdsByZone.entries.forEach { (zone, households) ->
            strategy.generateLocations(zone, households).forEach {
                it.first.attributes.location = it.second
            }
        }
    }

    fun <STAR> refactoredPopsyn(
        converter: (STAR) -> AREA,
        lambda: () -> CompletePopulationSynthesis<STAR, SynthesisHousehold<S, T>>,
    ) {
        val strategy = lambda()
        householdsByZone = strategy.synthesizeAll().mapKeys { converter(it.key) }
    }

    fun assignLocations(lambda: () -> AssignHouseholdLocations<AREA, SynthesisHousehold<S, T>>) {
        val strategy = lambda()
        householdsByZone.entries.forEach { (zone, households) ->
            households.forEach {
                it.attributes.location = strategy.generateLocation(zone, it)
            }
        }
    }

    fun assignTransitCardOwnership(lambda: () -> HouseholdAssignmentStep<S, T, Boolean>) {
        val strategy = lambda()
        households.addProgressBar("assign Transit Card").forEach { hh ->
            hh.members.forEach {
                context(hh, Random(it.personId)) {
                    it.hasTransitPass = strategy.assignForPerson(it)
                }
            }
        }
    }

    fun assignCars(generationStrategy: GenerateCars<S, T>, assignStrategy: AssignMainUser<S, T>) {
        households.addProgressBar("Generate Cars").forEach {
            val cars = generationStrategy.generate(it)
            it.cars += assignStrategy.assign(it, cars)
        }
        cars = households.flatMap { it.cars }
    }

    @Deprecated(
        "This implementation spawns a coroutine for each household, and only one strategy, thus not " +
            "being thread safe if the strategy is not thread safe. The current actitopp implementation matches that " +
            "risk group. Use assignActivitiesPartitioned instead. ",
    )
    fun assignActivities(lambda: () -> GenerateHouseholdActivitySchedule<S, T>) {
        val strategy = lambda()
        val localHouseholdCopy = households
        val progressBar = standardProgressBar("Generate Activities", localHouseholdCopy.size)
        runBlocking {
            localHouseholdCopy.map { household ->
                launch(Dispatchers.Default) {
                    val plans = strategy.generate(household)
                    household.members.zip(plans).forEach { (person, activities) ->
                        person.plannedActivities = activities
                    }
                    progressBar.step()
                }
            }.joinAll()
        }
        localHouseholdCopy.forEach { household ->
            household.forEach {
                activities[it] = it.plannedActivities
            }
        }
    }

    /**
     * Assigns Activities partitioned over the households.
     */
    fun assignActivitiesPartitioned(lambda: () -> GenerateHouseholdActivitySchedule<S, T>) {
        val workerCount = Runtime.getRuntime().availableProcessors()
        val localHouseholdCopy = households
        val progressBar = standardProgressBar("Generate Activities", (localHouseholdCopy.size / workerCount))
        runBlocking {
            (0 until workerCount).map { workerId ->

                async(Default) {
                    var localIndex = workerId
                    val threadLocalStrategy = lambda()
                    while (localIndex < localHouseholdCopy.size) {
                        val household = localHouseholdCopy[localIndex]
                        val generation = threadLocalStrategy.generate(household)
                        household.members.zip(generation).forEach { (person, activities) ->
                            person.plannedActivities = activities
                        }
                        localIndex += workerCount
                        if (workerId == 0) {
                            progressBar.stepBy(1)
                        }
                        progressBar.step()
                    }
                }
            }.joinAll()
        }
        localHouseholdCopy.forEach { household ->
            household.forEach {
                activities[it] = it.plannedActivities
            }
        }
    }

    fun writeStandardOutputCSV(path: Path) = writeStandardOutputCSV(OutputWriters.useDirectoryForCSV(path))
    fun writeStandardOutputCSV(targets: OutputWriters) {
        targets.run {
            householdWriter?.let { households.write(it) }
            personWriter?.let { people.write(it) }
            carWriter?.let { households.writeCars(it) }
            activityWriter?.let { activities.writeActivities(it) }
            fixedDestinationWriter?.let { fixedDestinations.writeFixedDestinations(it) }
            opportunitiesWriter?.let { opportunities.writeOpportunities(it) }
        }
    }
}

fun <AREA, S, T : MinimumPersonAttributes> SynthesisSteps<AREA, S, T>.assignEconomicStatus(
    lambda: () -> DetermineEconomicStatus<S, T>,
) where S : MinimumHouseholdAttributes, S : HasMutableEconomicStatus {
    val strategy = lambda()
    households.forEach { it.attributes.economicStatus = strategy.determineStatus(it) }
}

fun <AREA, S, T : MinimumPersonAttributes> SynthesisSteps<AREA, S, T>.assignAmountOfCars(
    lambda: () -> AssignmentStep<SynthesisHousehold<S, T>, Int>,
)
        where
              S : MinimumHouseholdAttributes,
              S : HasMutableNumberOfCars {
    val strategy = lambda()
    households.forEach {
        context(Random(it.id)) {
            it.attributes.amountOfCars = strategy.assign(it)
        }
    }
}
