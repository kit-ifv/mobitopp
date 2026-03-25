package domain.synthesis

import AssignmentStep
import HouseholdAssignmentStep
import domain.shared.behavior.AttractivenessModel
import domain.shared.datastructure.schedule.Activity
import domain.shared.location.Zone
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.householdlocation.AssignHouseholdLocations
import domain.synthesis.behavior.cars.ownership.AssignMainUser
import domain.synthesis.behavior.economicstatus.DetermineEconomicStatus
import domain.synthesis.behavior.cars.generation.GenerateCars
import domain.synthesis.behavior.householdlocation.GroupAssignHouseholdLocations
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.cars.SynthesisCar
import domain.synthesis.behavior.activityGeneration.GenerateHouseholdActivitySchedule
import domain.synthesis.SynthesisHousehold
import domain.synthesis.SynthesisPerson
import domain.synthesis.behavior.fixedDestinations.AssignFixedDestinationBuilder
import domain.synthesis.behavior.sharingmemberships.SharingMembershipsBuilder
import domain.synthesis.results.FixedDestinationElements
import domain.synthesis.results.OpportunityOutput
import domain.synthesis.results.fastcsv.OutputWriters
import domain.synthesis.results.fastcsv.write
import domain.synthesis.results.fastcsv.writeOpportunities
import domain.synthesis.results.fastcsv.writeActivities
import domain.synthesis.results.fastcsv.writeCars
import edu.kit.ifv.populationsynthesis.synthesis.CompletePopulationSynthesis
import kotlinx.coroutines.Dispatchers
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
    val households: List<SynthesisHousehold<S, T>> get() = householdsByZone.flatMap { it.value }
    val people get() = households.flatMap { it.members }
    var activities: List<Map<SynthesisPerson<*, *>, Collection<Activity>>> =
        listOf()
    var cars = listOf<SynthesisCar>()
    var fixedDestinations: List<FixedDestinationElements> = emptyList()

    /**
     * Within the scope of this step, the fixed destinations for the agents are generated. The structure of the assign
     * strategy is created in the [AssignFixedDestinationBuilder] class, which provides some convenience methods for
     * frequently assigned fixed destinations.
     */
    fun assignFixedDestinations(lambda: AssignFixedDestinationBuilder<Zone, S, T>.() -> Unit) {
        val fixedDestinationBuilder = AssignFixedDestinationBuilder<Zone, S, T>(attractivenessModel)
        fixedDestinationBuilder.apply(lambda)


        val allFixedDestinations = fixedDestinationBuilder.steps.flatMap { it.generateFixedDestinations(households) }
        allFixedDestinations.addProgressBar(
            "Assign Fixed Destinations"
        ).forEach { it.person.fixedDestinations[it.activityType] = it.location }
        fixedDestinations = allFixedDestinations
    }

    fun assignSharingMemberships(lambda: SharingMembershipsBuilder<T>.() -> Unit) {
        val builder = SharingMembershipsBuilder<T>().apply(lambda)
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

    fun assignEconomicStatus(lambda: () -> DetermineEconomicStatus<S, T>) {
        val strategy = lambda()
        households.forEach { it.economicStatus = strategy.determineStatus(it) }
    }

    fun assignAmountOfCars(lambda: () -> AssignmentStep<SynthesisHousehold<S, T>, Int>) {
        val strategy = lambda()
        households.addProgressBar(
            "Assign car amount"
        ).forEach { household ->
            context(Random(household.id)) {
                household.amountOfCars = strategy.assign(household)
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

    fun assignActivities(lambda: () -> GenerateHouseholdActivitySchedule<S, T>) {
        val strategy = lambda()
        val progressBar = standardProgressBar("Generate Activities", households.size)
        runBlocking {
            households.map { household ->
                launch(Dispatchers.Default) {
                    val plans = strategy.generate(household)
                    household.members.zip(plans).forEach { (person, activities) ->
                        person.plannedActivities = activities
                    }
                    progressBar.step()
                }
            }.joinAll()
        }
        activities = households.map { it.members.associateWith { it.plannedActivities } }
    }

    fun writeStandardOutputCSV(path: Path) = writeStandardOutputCSV(OutputWriters.useDirectory(path))
    fun writeStandardOutputCSV(targets: OutputWriters) {
        targets.run {
            householdWriter?.let { households.write(it) }
            personWriter?.let { people.write(it) }
            carWriter?.let { cars.writeCars(it) }
            activityWriter?.let { activities.writeActivities(it) }
            fixedDestinationWriter?.let { cars.writeCars(it) }
            opportunitiesWriter?.let { opportunities.writeOpportunities(it) }
        }

    }
}

