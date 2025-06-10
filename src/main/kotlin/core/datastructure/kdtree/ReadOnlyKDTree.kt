package core.datastructure.kdtree

import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

data class WithMetric<T, M : Comparable<M>>(val item: T, val metric: M)

fun <T> Collection<WithMetric<T, *>>.discardMetric(): List<T> {
    return map { it.item }
}

/**
 * An implementation of a K-D Tree providing a search function for arbitrary elements. The dimensions of the tree are
 * calculated automatically by the provided translations in the public constructor.
 */
class ReadOnlyKDTree<T : Any>(points: List<T>, firstAttribute: (T) -> Double, vararg attributes: (T) -> Double) {
    val dimension = attributes.size + 1 // Optional attributes plus the first attribute
    private val root: KDElement<T>

    init {

        require(points.isNotEmpty()) {
            "Building a K-D Tree with no input is useless"
        }
        val temp = { t: T -> (listOf(firstAttribute) + attributes).map { it(t) }.toDoubleArray() }
        val comparator = build(temp, dimension)
        root = if (points.size > 1) {
            KDTreeNode(points, comparator, Hypercube.unlimited(dimension))
        } else {
            KDTreeLeaf(points.first(), comparator.converter)
        }
    }

    fun nearestNeighbor(point: KDPoint): T {
        return nearestNeighbor(point) { it }
    }

    /**
     * Locates the closest leaf node of an [element].
     * @param element The element to be located in the tree.
     * @param converter A translation to turn the [element] into a multidimensional point, preferably matching the
     * multidimensional points of the tree elements.
     */
    fun <S> nearestNeighbor(element: S, converter: (S) -> KDPoint): T {
        return findUntil(element, converter).first().item
    }

    fun findUntil(doubleArray: DoubleArray): Sequence<WithMetric<T, Double>> {
        return findUntil(doubleArray) { it }
    }

    fun <S, M : Comparable<M>> findUntil(
        element: S,
        converter: (S) -> KDPoint,
        metric: (Double) -> M
    ): Sequence<WithMetric<T, M>> {
        require(converter(element).size == dimension) {
            "The conversion for element $element has ${
                converter(
                    element
                ).size
            } dimensions, but the Tree is $dimension dimensional"
        }
        // The elements of the tree are assigned a proximity score; The priority queue should use this score for insertion
        val comparator = Comparator<WithMetric<KDElement<T>, M>> { a, b ->
            a.metric.compareTo(b.metric)
        }
        val queue = PriorityQueue(comparator)
        queue.add(WithMetric(root, metric(root.distance(element, converter))))
        return sequence {
            while (queue.isNotEmpty()) {
                val result = queue.poll()

                if (result.item is KDTreeLeaf) {
                    // The result can only be updated if and only if a leaf node has been found.
                    // Intermediate nodes only provide a heuristic.
                    yield(WithMetric(result.item.point, result.metric))
                } else {
                    // Only an intermediate node will have children, which need to be added to the queue.
                    queue.addAll(
                        result.item.evaluate(element, converter).map { WithMetric(it.item, metric(it.metric)) }
                    )
                }
            }
        }
    }

    fun <S> findUntil(element: S, converter: (S) -> KDPoint): Sequence<WithMetric<T, Double>> {
        return findUntil(element, converter, { it })
    }
}

private fun <T> build(converter: (T) -> KDPoint, size: Int): ComparatorBlock<T> {
    val blocks = (0..<size).map { ComparatorBlock(it, Double::compareTo, converter) }
    blocks.zipWithNext { first, second -> first.next = second }
    blocks.last().next = blocks.first()
    return blocks.first()
}
typealias KDPoint = DoubleArray

private fun KDPoint.distanceTo(other: KDPoint): Double {
    require(this.size == other.size) { "Points must have the same dimension" }
    return sqrt(this.zip(other).sumOf { (a, b) -> (a - b).pow(2) })
}

private class ComparatorBlock<T>(val index: Int, val comparator: Comparator<Double>, val converter: (T) -> KDPoint) {

    lateinit var next: ComparatorBlock<T>
}

private fun <T> List<T>.splitByMedian(): Pair<List<T>, List<T>> {
    return subList(0, size / 2) to subList(size / 2, size)
}

private fun <T> List<T>.median(): T {
    return get(size / 2)
}

private class Hypercube(val min: KDPoint, val max: KDPoint) {
    override fun toString(): String {
        return "${min.joinToString { it.toString() }} ${max.joinToString { it.toString() }}"
    }

    fun closestDistance(to: KDPoint): Double {
        var distance = 0.0

        // Iterate over each dimension (axis)
        for (i in min.indices) {
            // If the point is outside the bounds of the hypercube along the current dimension,
            // calculate the distance to the closest boundary.
            if (to[i] < min[i]) {
                distance += (min[i] - to[i]).pow(2)
            } else if (to[i] > max[i]) {
                distance += (to[i] - max[i]).pow(2)
            }
        }

        return sqrt(distance)
    }

    fun copy() = Hypercube(min.copyOf(), max.copyOf())

    companion object {
        fun unlimited(size: Int): Hypercube {
            return Hypercube(
                DoubleArray(size) { Double.NEGATIVE_INFINITY },
                DoubleArray(size) { Double.POSITIVE_INFINITY }
            )
        }
    }
}

private sealed interface KDElement<T> {
    val point: T
    fun find(element: T): T
    fun elements(): List<T>
    fun <S> distance(element: S, converter: (S) -> KDPoint): Double
    fun <S> evaluate(element: S, metric: (S) -> KDPoint): List<WithMetric<KDElement<T>, Double>>
}

private class KDTreeLeaf<T>(override val point: T, val converter: (T) -> DoubleArray) : KDElement<T> {
    override fun find(element: T): T {
        return point
    }

    override fun elements(): List<T> {
        return listOf(point)
    }

    override fun <S> evaluate(element: S, metric: (S) -> KDPoint): List<WithMetric<KDElement<T>, Double>> {
        return listOf()
    }

    override fun <S> distance(element: S, converter: (S) -> KDPoint): Double {
        return converter(element).distanceTo(this.converter(point))
    }

    override fun toString(): String {
        return point.toString()
    }
}

private class KDTreeNode<T>(
    points: List<T>,
    comparatorBlock: ComparatorBlock<T>,

    private val bounds: Hypercube,

) : KDElement<T> {

    private val comparator: Comparator<Double> = comparatorBlock.comparator
    private val index: Int = comparatorBlock.index
    val converter: (T) -> DoubleArray = comparatorBlock.converter
    override val point: T

    override fun elements(): List<T> {
        return left.elements() + right.elements()
    }

    override fun <S> evaluate(element: S, metric: (S) -> KDPoint): List<WithMetric<KDElement<T>, Double>> {
        return listOf(
            WithMetric(left, left.distance(element, metric)),
            WithMetric(right, right.distance(element, metric))
        )
    }

    override fun <S> distance(element: S, converter: (S) -> KDPoint): Double {
        return bounds.closestDistance(converter(element))
    }

    //
    override fun find(element: T): T {
        return if (comparator.compare(converter(element)[index], pivot[index]) <= 0) {
            left.find(element)
        } else {
            right.find(element)
        }
    }

    private val left: KDElement<T>
    private val right: KDElement<T>

    private val pivot: KDPoint

    init {

        require(points.size > 1) {
            "A Intermediate node should not be created using only one point"
        }
        val sorted = points.sortedWith { a: T, b: T -> comparator.compare(converter(a)[index], converter(b)[index]) }
        point = sorted.median()
        pivot = converter(point)
        val (l, r) = sorted.splitByMedian()

        left = when (l.size) {
            1 -> KDTreeLeaf(l.first(), converter)
            else -> {
                val bounds = bounds.copy()
                bounds.max[index] = pivot[index]
                KDTreeNode(l, comparatorBlock.next, bounds)
            }
        }
        right = when (r.size) {
            1 -> KDTreeLeaf(r.first(), converter)
            else -> {
                val bounds = bounds.copy()
                bounds.min[index] = pivot[index]
                KDTreeNode(r, comparatorBlock.next, bounds)
            }
        }
    }

    override fun toString(): String {
        return "Hypercube: [$bounds]"
    }
}
