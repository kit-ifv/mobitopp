package utils.collections

/**
 * Print the given treelike structure with pretty indentation on the console.
 *
 * @param T the generic type of elements in the tree like structure
 * @param root the root of the tree to be printed
 * @param label a function to convert elements to string labels
 * @param expandNonCycleDuplicates whether duplicate elements (that do not form a cycle) should be expanded,
 * duplication is determined via [Collection.contains]
 * @param getChildren a function to obtain the children of an element
 */
fun <T> printAsTree(
    root: T,
    label: (T) -> String,
    expandNonCycleDuplicates: Boolean = false,
    getChildren: (T) -> List<T>
) {
    println(label(root))

    val elements = getChildren(root)
    val visited = mutableListOf(root)
    elements.forEachIndexed { index, element ->
        val isLast = index == elements.size - 1
        printTreeRecursive(
            element = element,
            toLabel = label,
            isLast = isLast,
            getChildren = getChildren,
            expandNonCycleDuplicates = expandNonCycleDuplicates,
            visited = visited
        )

        if (!isLast) {
            println(I_MARK)
        } else {
            println()
        }
    }
}

private const val T_MARK = "├─"
private const val L_MARK = "└─"
private const val I_MARK = "│ "
private const val INDENT = "  "

@Suppress("LongParameterList")
private fun <T> printTreeRecursive(
    element: T,
    toLabel: (T) -> String,
    prefix: String = "",
    isLast: Boolean = true,
    expandNonCycleDuplicates: Boolean,
    visited: MutableList<T> = mutableListOf(),
    getChildren: (T) -> List<T>
) {
    val label = toLabel(element)
    val mark = if (isLast) {
        L_MARK
    } else {
        T_MARK
    }

    // Detect cycle by checking if the current message has already been visited
    if (element in visited) {
        val errorType = if (expandNonCycleDuplicates) {
            "Cycle detected!"
        } else {
            "Duplicate"
        }
        println(
            "$prefix$mark [$errorType] $label".indentSubsequentLines(prefix = prefix)
        )
        return
    }

    // Add the current message to the set of visited elements
    println("$prefix$mark $label".indentSubsequentLines(prefix = prefix + " ".repeat(mark.length)))

    val children = getChildren(element)
    visited.add(element)
    if (children.isNotEmpty()) {
        val lastIndex = children.size - 1
        val childIndentMarks = prefix + if (isLast) INDENT else I_MARK

        children.forEachIndexed { index, child ->
            val isLastChild = index == lastIndex
            printTreeRecursive(
                child,
                toLabel,
                childIndentMarks,
                isLastChild,
                expandNonCycleDuplicates,
                visited,
                getChildren
            )
        }
    }

    if (expandNonCycleDuplicates) {
        // Remove the message from the visited set after finishing processing its children;
        // avoids cycles but allows diamond duplicates!
        visited.remove(element)
    } // Otherwise, keep element in visited to avoid reprinting non-cycle duplicates (e.g. diamond)
}
