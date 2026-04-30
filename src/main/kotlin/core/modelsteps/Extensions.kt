package core.modelsteps

///**
// * Wraps an [AbstractAddResourceStep] with binary caching support.
// */
//fun <E : Identifiable<I>, I> AddResourceStep<E, I>.cached(
//    binaryReader: BinaryReader<E>,
//    binaryWriter: BinaryWriter<E>,
//    sourcePath: Path,
//    cacheRootPath: Path = Path.of("data"),
//): CachedAddResourceStep<E, I> {
//    return CachedAddResourceStep(
//        binaryReader,
//        binaryWriter,
//        this,
//        originalSourcePath = sourcePath,
//        cacheRootPath = cacheRootPath
//    )
//}
