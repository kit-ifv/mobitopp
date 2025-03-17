package usecases.steps

import modeling.steps.AddResourceStep
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.Resource
import modeling.steps.asResource
import modeling.validation.Warning
import usecases.steps.binary.BinaryReader
import utils.Identifiable
import java.nio.file.Path
import kotlin.io.path.name

class LoadBinaryStep<MUTABLE : Identifiable<ID>, ID>(
    path: Path,
    parser: BinaryReader<MUTABLE>,
    override val repository: MutableRepository<MUTABLE, ID>,
    override val dependentRepositories: Set<Repository<*, *>>,

    ) : AddResourceStep<MUTABLE, ID>() {

    override val name: String = "load ${path.fileName}"
    override val resource: Resource<MUTABLE> = parser.fromBinary(path).asResource(name, path.name)

    override fun mockElementsForValidation(): List<MUTABLE> {
        return emptyList() // TODO I WILL NOT WRTIE A BINARY FILE ON MY OWN
    }

    override fun verifyInput(): Warning? {
        return null // TODO some reasonable validation.
    }
}