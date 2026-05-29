package utils.concurrent

fun <R> synchronizeAll(dependencies: Set<Any>, block: () -> R): R {
    // Sort the dependencies to avoid deadlock caused by inconsistent lock order
    val sortedDependencies = dependencies.sortedBy { it.hashCode() }

    // Lock on each dependent object
    return synchronizeNested(sortedDependencies, block)
}

private fun <R> synchronizeNested(dependencies: List<Any>, block: () -> R): R = if (dependencies.isEmpty()) {
    block()
} else {
    val dependency = dependencies.first()

    synchronized(dependency) {
        synchronizeNested(dependencies.drop(1), block)
    }
}
