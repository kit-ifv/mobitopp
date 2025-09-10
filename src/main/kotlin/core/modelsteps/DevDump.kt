package core.modelsteps

import application.steps.parser.csv.LoadHouseholdContext
import application.steps.parser.csv.LoadPersonsContext
import application.steps.parser.csv.LoadPlannedActivitiesContext
import application.steps.parser.csv.PersonCsvConfig
import application.steps.parser.csv.finishActivities
import application.steps.parser.csv.finishHouseholds
import application.steps.parser.csv.finishPersons
import application.steps.parser.csv.personCsvConfig
import domain.shared.datastructure.schedule.Activity
import domain.shared.enums.ActivityType
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.synthesis.data.ActivityId
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.PersonId
import domain.synthesis.parser.binary.BinaryActivityReader
import domain.synthesis.parser.binary.BinaryActivityWriter
import domain.synthesis.parser.binary.BinaryHouseholdReader
import domain.synthesis.parser.binary.BinaryHouseholdWriter
import domain.synthesis.parser.binary.BinaryPersonReader
import domain.synthesis.parser.binary.BinaryPersonWriter
import utils.CodePlan
import utils.Identifiable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.files.crc32
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.nameWithoutExtension


fun <E : Identifiable<I>, I> MutatingStep<E, I>.spawnFilterStep(predicate: (E) -> Boolean): FilterStep<E, I> {
    return object : FilterStep<E, I>() {
        override fun check(element: E): Boolean {
            return predicate(element)
        }

        override val repository: MutableRepository<E, I> = this@spawnFilterStep.repository
        override val dependentRepositories: Set<Repository<*, *>> = this@spawnFilterStep.dependentRepositories
        override val name: String = "Filter spawned from ${this@spawnFilterStep.name}"

        override fun verifyInput(): Warning? {
            return this@spawnFilterStep.verifyInput()
        }
    }
}

fun LoadPlannedActivitiesContext.activities(lambda : ActivityBuild.() -> Unit) {
    val builder = ActivityBuild(simulationSeed, personRepository::get, activityTypes)
    builder.apply(lambda)
    builder.executeOn(this)
    finishActivities()

}
class ActivityBuild(
    val seed: Long, val converter: (PersonId) -> MutablePerson?, activityCodes: CodePlan<ActivityType>
):LPCBuilder<MutablePlannedActivity, ActivityId>() {
    override val reader: BinaryReader<MutablePlannedActivity> = BinaryActivityReader(
        codeActivity = activityCodes,
        personConverter = converter,
        contextSimulationSeed = seed
    )
    override val writer: BinaryWriter<MutablePlannedActivity> = BinaryActivityWriter()

    override fun fromCSV(
        source: Path,
        lambda: context(Path) () -> AddResourceStep<MutablePlannedActivity, ActivityId>,
    ): FileBasedResourceStep<MutablePlannedActivity, ActivityId> {
        return context(source) {
            FileBasedResourceStep(source, lambda())
        }
    }
}

fun LoadHouseholdContext.households(lambda: HouseholdBuild.() -> Unit) {
    val builder = HouseholdBuild(simulationSeed, zoneRepository::getValue)
    builder.apply(lambda)
    builder.executeOn(this)
    finishHouseholds()
}

fun LoadPersonsContext.persons(lambda: PersonBuild.() -> Unit) {
    val lpcBuilder = PersonBuild(this.simulationSeed, householdRepository::get)
    lambda(lpcBuilder)
    lpcBuilder.executeOn(this)
    finishPersons()
}

//fun LoadPersonsContext.fromCSV(
//    path: Path,
//    lambda: PersonCsvConfig.() -> Unit,
//): FileBasedResourceStep<MutablePerson, PersonId> {
//    val personCsvConfig = PersonCsvConfig(path = path, incomeUnit = costUnit)
//    personCsvConfig.apply(lambda)
//    return FileBasedResourceStep(path, personCsvConfig(personCsvConfig))
//}

data class FileBasedResourceStep<E : Identifiable<I>, I>(
    val source: Path,
    val step: AddResourceStep<E, I>,
)

class HouseholdBuild(val seed: Long, val converter: (ZoneId) -> Zone) : LPCBuilder<MutableHousehold, HouseholdId>() {
    override val reader: BinaryReader<MutableHousehold> = BinaryHouseholdReader(converter, seed)
    override val writer: BinaryWriter<MutableHousehold> = BinaryHouseholdWriter()
    override fun fromCSV(
        source: Path,
        lambda: context(Path) () -> AddResourceStep<MutableHousehold, HouseholdId>,
    ): FileBasedResourceStep<MutableHousehold, HouseholdId> {
        return context(source) {
            FileBasedResourceStep(source, lambda(source))
        }
    }
}

class PersonBuild(val seed: Long, val converter: (HouseholdId) -> MutableHousehold?) :
    LPCBuilder<MutablePerson, PersonId>() {
    override val reader = BinaryPersonReader(converter, seed)
    override val writer: BinaryWriter<MutablePerson> = BinaryPersonWriter()

    override fun fromCSV(
        source: Path,
        lambda: context(Path) () -> AddResourceStep<MutablePerson, PersonId>,
    ): FileBasedResourceStep<MutablePerson, PersonId> {
        return context(source) {
            FileBasedResourceStep(source, lambda(source))
        }

    }


}

abstract class LPCBuilder<E : Identifiable<I>, I>() {
    abstract val reader: BinaryReader<E>
    abstract val writer: BinaryWriter<E>
    lateinit var source: AddResourceStep<E, I>
    var filter: ((E) -> Boolean)? = null


    private fun AddResourceStep<E, I>.cacheCool(cacheRootPath: Path, sourcePath: Path): AddResourceStep<E, I> {
        return this.cached(
            reader,
            writer,
            cacheRootPath = cacheRootPath,
            sourcePath = sourcePath

        )
    }

    abstract fun fromCSV(
        source: Path,
         lambda: context(Path) () -> AddResourceStep<E, I>,
    ): FileBasedResourceStep<E, I>

    fun FileBasedResourceStep<E, I>.disableCache(): AddResourceStep<E, I> {
        return step
    }

    fun FileBasedResourceStep<E, I>.enableCache(cacheRootPath: Path = Path.of("data")): AddResourceStep<E, I> {
        return step.cacheCool(cacheRootPath = cacheRootPath, sourcePath = source)
    }

    /**
     * Run the execute step that spawns the resource, and if a filter is defined, run the filter step afterwards.
     */
    fun executeOn(target: Context) {
        target.runStep {
            source
        }
        filter?.let {
            target.runStep { source.spawnFilterStep(it) }
        }

    }
}

fun <E : Identifiable<I>, I> AddResourceStep<E, I>.cached(
    binaryReader: BinaryReader<E>,
    binaryWriter: BinaryWriter<E>,
    sourcePath: Path,
    cacheRootPath: Path = Path.of("data"),
): CachedIntegration<E, I> {
    return CachedIntegration(
        binaryReader, binaryWriter, this, originalSourcePath = sourcePath, cacheRootPath = cacheRootPath
    )
}

class AddResourceStepBuilder {

}

class CachedIntegration<E : Identifiable<I>, I>(
    val binaryReader: BinaryReader<E>,
    val binaryWriter: BinaryWriter<E>,

    val originalStep: AddResourceStep<E, I>,

    private val cacheRootPath: Path,
    private val originalSourcePath: Path,
) : AddResourceStep<E, I>() {


    override val repository: MutableRepository<E, I> = originalStep.repository
    override val dependentRepositories: Set<Repository<*, *>> = originalStep.dependentRepositories
    override val name: String = "load ${originalSourcePath.fileName} with background cache${originalStep.name}"
    private val cacheFolder by lazy {
        cacheRootPath.resolve("data-cache").apply { createDirectories() }
    }

    val hasValidCacheEntry: Boolean by lazy {
        hasValidCache()
    }
    private val expectedCachePath = cacheFolder.resolve(originalSourcePath.nameWithoutExtension + ".bin")


    private fun hasValidCache(): Boolean {

        if (!expectedCachePath.exists()) return false
        val originalChecksum = originalSourcePath.crc32()
        val cachedChecksum = binaryReader.checksum(expectedCachePath)
        return cachedChecksum == originalChecksum
    }

    override val resource: Resource<E> by lazy {
        if (hasValidCacheEntry) cachedResource() else originalStep.resource
    }

    override fun execute() {
        super.execute()
        if (!hasValidCacheEntry) {
            binaryWriter.toBinary(
                expectedCachePath,
                repository.elements.toList(),
                checksum = originalSourcePath.crc32()
            )

        }
    }

    override fun mockElementsForValidation(): List<E> {
        return emptyList()
    }

    fun cachedResource(): Resource<E> {

        return BinaryFileResource(expectedCachePath, binaryReader)
    }

    override fun verifyInput(): Warning? {
        return null // TODO
    }


}


class BinaryFileResource<E>(
    private val path: Path,
    private val reader: BinaryReader<E>,
) : Resource<E> {
    override val name: String
        get() = path.fileName.toString()
    override val source: String
        get() = path.toString()
    override val elements: Sequence<E>
        get() = content.asSequence()

    val content by lazy {
        reader.fromBinary(path)
    }
}

