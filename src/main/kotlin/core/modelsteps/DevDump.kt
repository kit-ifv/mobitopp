package core.modelsteps

import application.steps.parser.csv.LoadHouseholdContext
import application.steps.parser.csv.LoadPersonsContext
import application.steps.parser.csv.PersonCsvConfig
import application.steps.parser.csv.csvResourceStep
import application.steps.parser.csv.finishHouseholds
import application.steps.parser.csv.finishPersons
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.PersonId
import domain.synthesis.parser.binary.BinaryPersonReader
import domain.synthesis.parser.binary.BinaryPersonWriter
import utils.Identifiable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.files.crc32
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.nameWithoutExtension

fun LoadHouseholdContext.households(lambda: LoadHouseholdContext.() -> Unit) {

    lambda()
    finishHouseholds()
}

fun <E : Identifiable<I>, I> MutatingStep<E, I>.spawnFilterStep(predicate: (E) -> Boolean): FilterStep<E, I>{
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

fun LoadPersonsContext.persons(lambda: LPCBuilder.() -> Unit) {
    val lpcBuilder = LPCBuilder(this.simulationSeed, householdRepository::getValue)
    lambda(lpcBuilder)
    runStep {
        lpcBuilder.source

    }
    val filter = lpcBuilder.filter
    if(filter != null) {
        val filterstep = lpcBuilder.source.spawnFilterStep(filter)
        runStep {
            filterstep
        }
    }
    finishPersons()
}

fun LoadPersonsContext.fromCSV(
    path: Path,
    lambda: PersonCsvConfig.() -> Unit,
): FileBasedResourceStep<MutablePerson, PersonId> {
    val personCsvConfig = PersonCsvConfig(path = path, incomeUnit = costUnit)
    personCsvConfig.apply(lambda)
    return FileBasedResourceStep(path, csvResourceStep(personCsvConfig))
}

data class FileBasedResourceStep<E : Identifiable<I>, I>(
    val source: Path,
    val step: AddResourceStep<E, I>
)

class LPCBuilder(val seed: Long, val converter: (HouseholdId) -> MutableHousehold) {
    lateinit var source: AddResourceStep<MutablePerson, PersonId>
    var filter: ((MutablePerson) -> Boolean)? = null


    private fun AddResourceStep<MutablePerson, PersonId>.cacheCool(cacheRootPath: Path, sourcePath: Path): AddResourceStep<MutablePerson, PersonId> {
        return this.cached(
            BinaryPersonReader(converter, seed), BinaryPersonWriter(),
            cacheRootPath = cacheRootPath,
            sourcePath = sourcePath

        )
    }
    fun FileBasedResourceStep<MutablePerson, PersonId>.enableCache(cacheRootPath: Path = Path.of("data")): AddResourceStep<MutablePerson, PersonId> {
        return step.cacheCool(cacheRootPath = cacheRootPath, sourcePath = source)
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
            binaryWriter.toBinary(expectedCachePath, repository.elements.toList(), checksum = originalSourcePath.crc32())

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

