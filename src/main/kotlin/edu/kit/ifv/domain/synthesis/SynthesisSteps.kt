package edu.kit.ifv.domain.synthesis

import edu.kit.ifv.binary.BinaryWriter
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Activity
import edu.kit.ifv.domain.synthesis.attributes.household.HasMutableEconomicStatus
import edu.kit.ifv.domain.synthesis.attributes.household.HasMutableNumberOfCars
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.HasMutableTransitPass
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.behavior.activitygeneration.GenerateHouseholdActivitySchedule
import edu.kit.ifv.domain.synthesis.behavior.cars.SynthesisCar
import edu.kit.ifv.domain.synthesis.behavior.cars.generation.GenerateCars
import edu.kit.ifv.domain.synthesis.behavior.cars.ownership.AssignMainUser
import edu.kit.ifv.domain.synthesis.behavior.economicstatus.DetermineEconomicStatus
import edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.AssignFixedDestinationBuilder
import edu.kit.ifv.domain.synthesis.behavior.householdlocation.AssignHouseholdLocations
import edu.kit.ifv.domain.synthesis.behavior.sharingmemberships.SharingMembershipsBuilder
import edu.kit.ifv.domain.synthesis.results.FixedDestinationElements
import edu.kit.ifv.domain.synthesis.results.binary.ActivitiesBinaryRecord
import edu.kit.ifv.domain.synthesis.results.binary.FixedDestinationBinaryRecord
import edu.kit.ifv.domain.synthesis.results.binary.StandardFixedDestinationWriter
import edu.kit.ifv.domain.synthesis.results.binary.StandardOutputBinaryCarWriter
import edu.kit.ifv.domain.synthesis.results.binary.StandardSynthesisBinaryActivitiesWriter
import edu.kit.ifv.domain.synthesis.results.binary.SynthesisCarBinaryRecord
import edu.kit.ifv.domain.synthesis.results.binary.writeActivitiesBinary
import edu.kit.ifv.domain.synthesis.results.binary.writeCarsBinary
import edu.kit.ifv.domain.synthesis.results.binary.writeFixedDestinations
import edu.kit.ifv.domain.synthesis.results.binary.writeHouseholdsBinary
import edu.kit.ifv.domain.synthesis.results.binary.writePersonsBinary
import edu.kit.ifv.domain.synthesis.results.fastcsv.OutputWriters
import edu.kit.ifv.domain.synthesis.results.fastcsv.writers.writeActivities
import edu.kit.ifv.domain.synthesis.results.fastcsv.writers.writeCars
import edu.kit.ifv.domain.synthesis.results.fastcsv.writers.writeFixedDestinations
import edu.kit.ifv.domain.synthesis.results.fastcsv.writers.writeHouseholds
import edu.kit.ifv.domain.synthesis.results.fastcsv.writers.writePersons
import edu.kit.ifv.populationsynthesis.synthesis.CompletePopulationSynthesis
import edu.kit.ifv.utils.collections.addProgressBar
import edu.kit.ifv.utils.collections.standardProgressBar
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.file.Path
import kotlin.random.Random

/**
 * A collection of steps that are performed during synthesis. This class captures all the fields that we currently
 * assume to be guaranteed present, either as input or during some point of the step execution.
 * Currently we assume that: A) A immutable collection of Areas (named Zones since 99% of projects use Traffic analysis
 * zones).
 * B) An immutable collection of survey households (There may be changes in the future, as there are scenarios where
 * such a household pool is unneccessary)
 *
 * C) An output directory
 *
 * THen we have fields that are going to mutate during the execution of the steps.
 * 1) HouseholdsByZone, even though slightly misnomed because AREA can be arbitrary we can safely assume that households
 * at some point end up in an assignment where they are assigned to an area.
 *
 * 2) households as reactive field over the keys of householdsbyzone
 * 3) persons as a reactive field over the members of the households.
 * 4) cars associated to a household (but only through their internal id not by a mapping held in this class)
 * 5) Fixed destinations that some people have, like a fixed work or education location.
 * 6) Persons get a collection of generated activities, usually a week.
 */
class SynthesisSteps<AREA, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
    val zones: List<AREA>,
    val surveyHouseholds: Collection<ISurveyHousehold<S, T>>,
    val outputDirectory: Path,
    @Suppress("MagicNumber")
    var randomProvider: SynthesisRandomProvider<S, T> = SeededProvider(42L) { x, y ->
        Random(x + y.hashCode())
    },
) {

    lateinit var householdsByZone: Map<AREA, List<SynthesisHousehold<S, T>>>

    /**
     * returns the current list of households within the population synthesis. Note that this is a view that
     * constructs the household list from the assigned zones by flattening and has no backing field.
     *
     * If you require frequent access create a local variable for performance.
     */
    val households: List<SynthesisHousehold<S, T>> get() = householdsByZone.flatMap { it.value }

    @Deprecated("Be mindful when using this getter in a hot loop")
    val people get() = households.flatMap { it.members }
    val activities: MutableMap<SynthesisPerson<*, *>, Collection<Activity>> = mutableMapOf()
    var cars = listOf<SynthesisCar>()
    var fixedDestinations: List<FixedDestinationElements> = emptyList()

    /**
     * Within the scope of this step, the fixed destinations for the agents are generated. The structure of the assign
     * strategy is created in the [AssignFixedDestinationBuilder] class, which provides some convenience methods for
     * frequently assigned fixed destinations.
     */
    fun assignFixedDestinations(supplier: AssignFixedDestinationBuilder<AREA, S, T>.() -> Unit) {
        val fixedDestinationBuilder = AssignFixedDestinationBuilder<AREA, S, T>()
        fixedDestinationBuilder.apply(supplier)

        val allFixedDestinations = fixedDestinationBuilder.steps.flatMap { it.generateFixedDestinations(households) }
        allFixedDestinations.addProgressBar(
            "Assign Fixed Destinations",
        ).forEach { it.person.fixedDestinations[it.activityType] = it.location }
        fixedDestinations = allFixedDestinations
    }

    fun assignSharingMemberships(supplier: SharingMembershipsBuilder<S, T>.() -> Unit) {
        val builder = SharingMembershipsBuilder<S, T>().apply(supplier)
        val steps = builder.build()
        households.addProgressBar("assign sharing memberships").forEach { hh ->
            hh.members.forEach {
                context(randomProvider.provideFor(it)) {
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

    /**
     * Runs population synthesis with a key conversion from [STAR] to [AREA].
     *
     * Use this overload when the synthesis strategy returns household assignments keyed
     * by a different area representation than this configuration uses.
     *
     * @param STAR area key type produced by the population synthesis.
     * @param converter converts synthesis keys to [AREA] keys.
     * @param supplier supplies the population synthesis strategy.
     */
    fun <STAR> synthesize(
        converter: (STAR) -> AREA,
        supplier: () -> CompletePopulationSynthesis<STAR, SynthesisHousehold<S, T>>,
    ) {
        val strategy = supplier()
        householdsByZone = strategy.synthesizeAll().mapKeys { converter(it.key) }
    }

    /**
     * Runs population synthesis when the synthesis strategy already uses [AREA] keys.
     *
     * The synthesized household assignments are stored in [householdsByZone].
     *
     * @param supplier supplies the population synthesis strategy.
     */
    fun synthesize(supplier: () -> CompletePopulationSynthesis<AREA, SynthesisHousehold<S, T>>) {
        val strategy = supplier()
        householdsByZone = strategy.synthesizeAll()
    }

    /**
     * Assigns a location to each synthesized household.
     *
     * The supplied strategy is called for every household in every entry of
     * [householdsByZone].
     *
     * @param supplier supplies the household-location assignment strategy.
     */
    fun assignLocations(supplier: () -> AssignHouseholdLocations<AREA, SynthesisHousehold<S, T>>) {
        val strategy = supplier()
        householdsByZone.entries.forEach { (zone, households) ->
            households.forEach {
                it.attributes.location = strategy.generateLocation(zone, it)
            }
        }
    }

    /**
     * Generates cars for each household and assigns their main users.
     *
     * Generated and assigned cars are added to each household's `cars` collection. The
     * flattened result is stored in [cars].
     *
     * @param generationStrategy strategy used to generate cars for a household.
     * @param assignStrategy strategy used to assign generated cars to main users.
     */
    fun spawnCars(generationStrategy: GenerateCars<S, T>, assignStrategy: AssignMainUser<S, T>) {
        households.addProgressBar("Generate Cars").forEach {
            val cars = generationStrategy.generate(it)
            it.cars += assignStrategy.assign(it, cars)
        }
        cars = households.flatMap { it.cars }
    }

    @Deprecated(
        "This implementation spawns a coroutine for each household, and only one strategy, thus not " +
            "being thread safe if the strategy is not thread safe. The current actitopp implementation matches that " +
            "risk group. Use assignActivities instead. ",
    )
    fun assignActivitiesUnconstrained(supplier: () -> GenerateHouseholdActivitySchedule<S, T>) {
        val strategy = supplier()
        val localHouseholdCopy = households
        val progressBar = standardProgressBar("Generate Activities", localHouseholdCopy.size)
        runBlocking {
            localHouseholdCopy.map { household ->
                launch(Default) {
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
     * Assigns activity schedules to household members using worker-local generation
     * strategies.
     *
     * The method creates one coroutine per available processor. Each coroutine receives
     * its own strategy instance from [supplier] and processes every `workerCount`-th
     * household. This avoids sharing the strategy instance between workers.
     *
     * Assigned schedules are written to each person's `plannedActivities` field and then
     * collected into [activities].
     *
     * @param supplier supplies a new activity-generation strategy for a worker.
     */
    fun assignActivities(supplier: () -> GenerateHouseholdActivitySchedule<S, T>) {
        val workerCount = Runtime.getRuntime().availableProcessors()
        val localHouseholdCopy = households // Keep a local copy, because otherwise each thread access would go
        // through the getter function
        val progressBar = standardProgressBar("Generate Activities", (localHouseholdCopy.size))
        runBlocking {
            (0 until workerCount).map { workerId ->

                async(Default) {
                    var localIndex = workerId
                    val threadLocalStrategy = supplier()
                    while (localIndex < localHouseholdCopy.size) {
                        val household = localHouseholdCopy[localIndex]
                        val generation = threadLocalStrategy.generate(household)
                        household.members.zip(generation).forEach { (person, activities) ->
                            person.plannedActivities = activities
                        }
                        localIndex += workerCount
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

    /**
     * Writes the standard CSV output files into [path].
     *
     * @param path directory used by [OutputWriters.useDirectoryForCSV].
     */
    fun writeStandardOutputCSV(path: Path) = writeStandardOutputCSV(OutputWriters.useDirectoryForCSV(path))

    /**
     * Writes the standard outputs(persons, households, activities, cars, fixedDestinations) into files at the
     * [outputPath].
     */
    fun writeStandardOutputBinary(
        personWriter: BinaryWriter<SynthesisPerson<S, T>>,
        householdWriter: BinaryWriter<SynthesisHousehold<S, T>>,
        activityWriter: BinaryWriter<ActivitiesBinaryRecord> = StandardSynthesisBinaryActivitiesWriter,
        carWriter: BinaryWriter<SynthesisCarBinaryRecord> = StandardOutputBinaryCarWriter,
        fixedDestinationWriter: BinaryWriter<FixedDestinationBinaryRecord> = StandardFixedDestinationWriter,
        outputPath: Path = this.outputDirectory.resolve("binary"),
    ) {
        writePersonsBinary(outputPath, personWriter, people)
        writeHouseholdsBinary(outputPath, householdWriter, households)
        writeActivitiesBinary(outputPath, activityWriter, activities)
        writeCarsBinary(outputPath, carWriter, households)
        writeFixedDestinations(outputPath, fixedDestinationWriter, fixedDestinations)
    }

    /**
     * Writes available synthesis results to the configured output writers.
     *
     * A result is written only when the corresponding writer in [targets] is present.
     *
     * @param targets output writer configuration.
     */
    fun writeStandardOutputCSV(targets: OutputWriters) {
        targets.run {
            householdWriter?.let { households.writeHouseholds(it) }
            personWriter?.let { people.writePersons(it) }
            carWriter?.let { households.writeCars(it) }
            activityWriter?.let { activities.writeActivities(it) }
            fixedDestinationWriter?.let { fixedDestinations.writeFixedDestinations(it) }
        }
    }
}

/**
 * Assigns an economic status to each household.
 *
 * This is an extension because economic status is only available when the household
 * attributes implement [HasMutableEconomicStatus].
 *
 * @param S household attribute type.
 * @param T person attribute type.
 * @param supplier supplies the economic-status strategy.
 */
fun <S, T : MinimumPersonAttributes> SynthesisSteps<*, S, T>.assignEconomicStatus(
    supplier: () -> DetermineEconomicStatus<S, T>,
) where S : MinimumHouseholdAttributes, S : HasMutableEconomicStatus {
    val strategy = supplier()
    households.forEach { it.attributes.economicStatus = strategy.determineStatus(it) }
}

/**
 * Assigns the number of cars to each household.
 *
 * This is an extension because the number-of-cars field is only available when the
 * household attributes implement [HasMutableNumberOfCars]. The random context is
 * seeded with the household `id`.
 *
 * @param S household attribute type.
 * @param T person attribute type.
 * @param supplier supplies the assignment strategy.
 */
fun <S, T : MinimumPersonAttributes> SynthesisSteps<*, S, T>.assignAmountOfCars(
    supplier: () -> AssignmentStep<SynthesisHousehold<S, T>, Int>,
)
        where
              S : MinimumHouseholdAttributes,
              S : HasMutableNumberOfCars {
    val strategy = supplier()
    households.forEach {
        context(randomProvider.provideFor(it)) {
            it.attributes.amountOfCars = strategy.assign(it)
        }
    }
}

/**
 * Assigns transit-pass ownership to each synthesized person.
 *
 * This is an extension because transit-pass ownership is only available when the
 * person attributes implement [HasMutableTransitPass]. The assignment strategy is
 * evaluated in a household context and a random context seeded with the person's
 * `personId`.
 *
 * @param S household attribute type.
 * @param T person attribute type.
 * @param supplier supplies the household-aware assignment strategy.
 */
fun <S : MinimumHouseholdAttributes, T> SynthesisSteps<*, S, T>.assignTransitCardOwnership(
    supplier: () -> HouseholdAssignmentStep<S, T, Boolean>,
) where T : HasMutableTransitPass,
        T : MinimumPersonAttributes {
    val strategy = supplier()
    households.addProgressBar("assign Transit Card").forEach { hh ->
        hh.members.forEach {
            context(hh, randomProvider.provideFor(it)) {
                it.attributes.hasTransitPass = strategy.assignForPerson(it)
            }
        }
    }
}
