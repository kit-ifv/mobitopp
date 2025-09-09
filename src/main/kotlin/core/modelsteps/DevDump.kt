package core.modelsteps

import application.steps.parser.csv.LoadHouseholdContext
import application.steps.parser.csv.LoadPersonsContext
import application.steps.parser.csv.finishHouseholds
import application.steps.parser.csv.finishPersons
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.PersonId
import utils.Identifiable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.files.crc32
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension

fun LoadHouseholdContext.households(lambda: LoadHouseholdContext.() -> Unit) {

    lambda()
    finishHouseholds()
}
fun LoadPersonsContext.persons(lambda: LPCBuilder.() -> Unit) {
    val lpcBuilder = LPCBuilder(this.simulationSeed,  householdRepository::get)
    lambda(lpcBuilder)
    finishPersons()
}

class LPCBuilder(val seed: Long, val map: Map<HouseholdId, MutableHousehold>) {
    fun fromFile(path: Path, lambda: (Path) -> AddResourceStep<MutablePerson, PersonId>) : AddResourceStep<MutablePerson, PersonId> {
        return lambda(path)
    }
}

fun <E : Identifiable<I>, I> AddResourceStep<E, I>.cached(
    binaryReader: BinaryReader<E>,
    binaryWriter: BinaryWriter<E>,
    path: Path
) : CachedIntegration<E, I> {
    return CachedIntegration(
        binaryReader,binaryWriter, this, path = path
    )
}
class AddResourceStepBuilder {

}

class CachedIntegration<E : Identifiable<I>, I>(
    val binaryReader: BinaryReader<E>,
    val binaryWriter: BinaryWriter<E>,

    val originalStep: AddResourceStep<E, I>,

    private val rootCachePath: Path = Path.of("data"),
    private val path: Path,
    ) : AddResourceStep<E, I>() {



    override val repository: MutableRepository<E, I> = originalStep.repository
    override val dependentRepositories: Set<Repository<*, *>> = originalStep.dependentRepositories
    override val name: String = "load ${path.fileName} with background cache${originalStep.name}"
    private val cacheFolder by lazy {
        rootCachePath.resolve("data-cache").apply { createDirectories() }
    }

    val hasValidCacheEntry: Boolean by lazy {
        hasValidCache()
    }

    private fun hasValidCache(): Boolean {
        val candidate = cacheFolder.listDirectoryEntries().find { it.nameWithoutExtension == path.nameWithoutExtension }
        if (candidate == null) return false
        val originalChecksum = path.crc32()
        val cachedChecksum = candidate.crc32()
        return cachedChecksum == originalChecksum
    }

    override val resource: Resource<E> by lazy {
        if (hasValidCacheEntry) cachedResource() else originalStep.resource
    }

    override fun execute() {
        super.execute()
        if (!hasValidCacheEntry) {
            binaryWriter.toBinary(path, repository.elements.toList(), checksum = path.crc32())

        }
    }

    override fun mockElementsForValidation(): List<E> {
        return emptyList()
    }

    fun cachedResource(): Resource<E> {
        val candidate = cacheFolder.listDirectoryEntries().first { it.nameWithoutExtension == path.nameWithoutExtension }
        TODO("Apply filter to binary resource")
        return BinaryFileResource(candidate, binaryReader)
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

