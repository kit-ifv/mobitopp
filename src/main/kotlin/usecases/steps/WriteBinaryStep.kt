package usecases.steps

import modeling.steps.ForAllStep
import modeling.steps.Repository
import modeling.validation.Warning
import usecases.steps.binary.BinaryWriter
import utils.Identifiable
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

    override fun mockBehavior(): Warning? {
        return null
    }
}
