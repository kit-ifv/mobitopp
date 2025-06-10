package core.modelsteps.binary

import core.modelsteps.AddResourceStep
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.Resource
import core.modelsteps.Warning
import core.modelsteps.asResource
import utils.Identifiable
import utils.binary.BinaryReader
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
