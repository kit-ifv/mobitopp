import domain.shared.behavior.AttractivenessModel
import domain.shared.datastructure.schedule.Activity
import domain.shared.location.Zone
import domain.synthesis.AreaIPUCSVOutput
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.AssignHouseholdLocations
import domain.synthesis.behavior.DetermineEconomicStatus
import domain.synthesis.behavior.GenerateCars
import domain.synthesis.behavior.GroupAssignHouseholdLocations
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SynthesisCar
import domain.synthesis.behavior.activityGeneration.GenerateHouseholdActivitySchedule
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.behavior.fixedDestinations.AssignFixedDestinationBuilder
import domain.synthesis.behavior.householdgeneration.HierarchicalPopulationSynthesisDeprecated
import domain.synthesis.behavior.sharingmemberships.SharingMembershipsBuilder
import domain.synthesis.results.FixedDestinationElements
import domain.synthesis.results.OpportunityOutput
import edu.kit.ifv.populationsynthesis.synthesis.CompletePopulationSynthesis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.collections.addProgressBar
import utils.collections.standardProgressBar
import java.nio.file.Path
import kotlin.random.Random

class SynthesisSteps<AREA, S: MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
    val zones: List<AREA>,
    val surveyHouseholds: Collection<ISurveyHousehold<S, T>>,
    val attractivenessModel: AttractivenessModel,
    val outputDirectory: Path,
    val opportunities: List<OpportunityOutput>,
) {
//    private val zoneMapping by lazy { zones.associateBy { it.id } }
//
//    fun getZone(zoneId: ZoneId) = zoneMapping[zoneId]
//        ?: throw NoSuchElementException("There is no zone with id $zoneId in the mapping")

    lateinit var householdsByZone: Map<AREA, List<SynthesisHousehold<S, T>>>
    val households get() = householdsByZone.flatMap { it.value }
    val people get() = households.flatMap { it.members }
    var activities: List<Map<SynthesisPerson<*>, Collection<Activity>>> =
        listOf()
    var cars = listOf<SynthesisCar>()
    var fixedDestinations: List<FixedDestinationElements> = emptyList()

    /**
     * Within the scope of this step, the fixed destinations for the agents are generated. The structure of the assign
     * strategy is created in the [domain.synthesis.behavior.fixedDestinations.AssignFixedDestinationBuilder] class, which provides some convenience methods for
     * frequently assigned fixed destinations.
     */
    fun assignFixedDestinations(lambda: AssignFixedDestinationBuilder<Zone, T>.() -> Unit) {
        val fixedDestinationBuilder = AssignFixedDestinationBuilder<Zone, T>(attractivenessModel)
        fixedDestinationBuilder.apply(lambda)
        val allFixedDestinations = fixedDestinationBuilder.steps.flatMap { it.generateFixedDestinations(people) }
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

    fun synthesizePopulation() {
    }

    @Deprecated("This step must be refactored.")
    fun populationSynthesis(
        verification: Boolean = true,
        writeResults: Boolean = false,
        supplier: () -> HierarchicalPopulationSynthesisDeprecated<AREA, ISurveyHousehold<S, T>>,
    ) {
        val algorithm = supplier()
        val output = algorithm.synthesizeAll()
        householdsByZone = output.mapValues { it.value.map { it.toSynthesisHousehold() } }

        if (verification) {
            println(
                "Population Synthesis has error: ${
                    algorithm.ruleProvider.verify(
                        output
                    )
                }"
            )
            algorithm.ruleProvider.evaluate(output)
        }
        if (writeResults) {
            outputDirectory.resolve("IPUResults.csv").let {
                AreaIPUCSVOutput.writeCSVToFile(it, algorithm.ruleProvider.evaluate(output))
            }
        }
    }

    // TODO speaking type parameter names
//    @Deprecated("Use synthesis library instead.")
//    fun synthesis(
//        randsums: Map<AREA, List<Rule<ISurveyHousehold<out T>>>>,
//        lambda: () -> HouseholdSynthesis<Zone, ISurveyHousehold<out T>, SynthesisHousehold<out T>>,
//    ) {
//        val generator = lambda()
//        householdsByZone = generator.synthesize(surveyHouseholds, randsums)
//    }

    // TODO refactor, use or discard this method
    fun assignLocationsForAll(lambda: () -> GroupAssignHouseholdLocations<in AREA, SynthesisHousehold<S, T>>) {
        val strategy = lambda()

        householdsByZone.entries.forEach { (zone, households) ->
            strategy.generateLocations(zone, households).forEach {
                it.first.location = it.second
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
                it.location = strategy.generateLocation(zone, it)
            }
        }
    }

    fun assignEconomicStatus(lambda: () -> DetermineEconomicStatus<in T>) {
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

    fun assignTransitCardOwnership(lambda: () -> AssignmentStep<SynthesisHousehold<S, T>, List<Boolean>>) {
        val strategy = lambda()
        households.addProgressBar("assign Transit Car").forEach { hh ->

            val transitPasses = context( Random(hh.id)){
                strategy.assign(hh)
            }

            hh.zip(transitPasses).forEach { (person, decision) ->
                person.hasTransitPass = decision

            }
            hh.members.forEach {
                context(Random(it.personId)) {
                    it.hasTransitPass = strategy.assign(it)
                }
            }
        }
    }

    fun generateCars(strategy: GenerateCars<in T>) {
        households.addProgressBar("Generate Cars").forEach { it.cars += strategy.generate(it) }
        cars = households.flatMap { it.cars }
    }

    fun assignActivities(lambda: () -> GenerateHouseholdActivitySchedule<S, in T>) {
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
//        households.addProgressBar("Generate Activities").forEach { h ->
//            val output = strategy.generate(h)
//            output.entries.forEach { (k, v) ->
//                k.plannedActivities = v
//            }
//        }

        activities = households.map { it.members.associateWith { it.plannedActivities } }
    }
}
