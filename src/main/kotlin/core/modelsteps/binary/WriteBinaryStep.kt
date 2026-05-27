package core.modelsteps.binary

import core.modelsteps.ForAllStep
import core.modelsteps.Repository
import core.modelsteps.Warning
import utils.Identifiable
import utils.binary.BinaryWriter
import java.nio.file.Path

class WriteBinaryStep<READONLY : Identifiable<ID>, ID>(
    val path: Path,
    val writer: BinaryWriter<READONLY>,
    override val repository: Repository<READONLY, ID>,

) : ForAllStep<READONLY, ID>() {
    override val name: String = "Write Binary ${repository.name}"
    override val dependentRepositories: Set<Repository<*, *>> =
        emptySet() // There is no need for dependent repositories, the objects are already there

    override fun processAll(element: Collection<READONLY>) {
        writer.toBinary(path, element)
    }

    override fun verifyInput(): Warning? {
        return null // TODO("Not yet implemented")
    }

    override fun mockBehavior(): Warning? = null
}
