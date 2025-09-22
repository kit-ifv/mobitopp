package domain.jackson

import core.modelsteps.AddResourceStep
import core.modelsteps.BinaryFileResource
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.Resource
import core.modelsteps.Warning
import domain.synthesis.parser.ActivitiesColumns
import utils.Identifiable
import utils.binary.BinaryReader
import utils.csv.CsvParser
import utils.csv.int
import utils.csv.long
import java.io.DataOutputStream
import java.nio.file.Path


interface Simplifiable<TO: BinaryWritable> {
    fun simplify() : TO
}

interface BinaryWritable{
    fun writeTo(outStream: DataOutputStream)
}

data class CarBinaryRecord(
    val id: Long,
    val ownerId: Long,
    val seats: Int,
    val mainUserId: Long,
    val segmentCode: Int,
    val engineCode: Int,
): BinaryWritable {
    override fun writeTo(outStream: DataOutputStream) {
        outStream.run {
            writeLong(id)
            writeLong(ownerId)
            writeInt(seats)
            writeLong(mainUserId)
            writeInt(segmentCode)
            writeInt(engineCode)
        }
    }
}

data class ActivityBinaryRecord(
    val id: Long,
    val personId: Long,
    val observedTripDuration: Int,
    val startTime: Long,
    val duration: Int,
    val activityCode: Int,
): BinaryWritable {
    override fun writeTo(outStream: DataOutputStream) {
        outStream.run {
            writeLong(id)
            writeLong(personId)
            writeInt(observedTripDuration)
            writeLong(startTime)
            writeInt(duration)
            writeInt(activityCode)
        }
    }
}

class GuaranteedCache<E : Identifiable<I>, I>(
    val cacheFilePath: Path,
    val binaryReader: BinaryReader<E>,
    val originalFilePath: Path,
): AddResourceStep<E, I> {



    private val binaryResource: BinaryFileResource<E> = BinaryFileResource(
        path = cacheFilePath,
        reader = binaryReader
    )
    override val resource: Resource<E> by lazy {
//        resourceProtect()
        binaryResource
    }

//    private fun resourceProtect() {
//        if originalFilePath.crc32() != binaryReader.checksum(cacheFilePath)
//
//
//    }

    fun convertOriginalElement() {

    }

    override val repository: MutableRepository<E, I>
        get() = TODO("Not yet implemented")
    override val dependentRepositories: Set<Repository<*, *>>
        get() = TODO("Not yet implemented")
    override val name: String
        get() = TODO("Not yet implemented")

    override fun verifyInput(): Warning? {
        TODO("Not yet implemented")
    }

    override fun mockBehavior(): Warning? {
        TODO("Not yet implemented")
    }
}
made