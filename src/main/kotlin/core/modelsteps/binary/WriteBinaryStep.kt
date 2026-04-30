package core.modelsteps.binary

//class WriteBinaryStep<READONLY : Identifiable<ID>, ID>(
//    val path: Path,
//    val writer: BinaryWriter<READONLY>,
//    override val repository: Repository<READONLY, ID>,
//
//) : ForAllStep<READONLY, ID>() {
//    override val name: String = "Write Binary ${repository.name}"
//    override val dependentRepositories: Set<Repository<*, *>> =
//        emptySet() // There is no need for dependent repositories, the objects are already there
//
//    override fun processAll(element: Collection<READONLY>) {
//        writer.toBinary(path, element)
//    }
//
//    override fun verifyInput(): Warning? {
//        return null // TODO("Not yet implemented")
//    }
//
//    override fun mockBehavior(): Warning? {
//        return null
//    }
//}
//
//fun <C: Context, E: Identifiable<I>, I> C.writeBinary(
//    path: Path,
//    writer: BinaryWriter<E>,
//    repository: Repository<E, I>,
//    name: String = "write repo content ${repository.name} to binary ${path.fileName}",
//    validation: Validation<C> = emptyList(),
//) = forAllStep(
//    name,
//    repository,
//    emptySet(),
//    validation + { validateFileReadAccess(path, true, "binary cache file ${path.fileName}") }
//) { elements ->
//    writer.toBinary(path, elements)
//}
