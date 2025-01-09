package datastructure

import java.util.*
import kotlin.math.pow

/**
 * An implementation of a K-D Tree providing a search function for arbitrary elements. The dimensions of the tree are
 * calculated automatically by the provided translations in the public constructor.
 */
class ReadOnlyKDTree<T> (points: List<T>, firstAttribute: (T) -> Double, vararg attributes: (T) -> Double) {
    val dimension = attributes.size + 1 // Optional attributes plus the first attribute
    private val root: KDTreeNode<T>
    init {
        val temp = { t: T -> (listOf(firstAttribute) + attributes).map { it(t) }.toDoubleArray() }
        val comparator = build(temp, dimension)
        root = KDTreeNode(points, comparator, Hypercube.unlimited(dimension))
    }

    fun find(point: Point): T? {
        return find(point) { it }
    }

    /**
     * Locates the closest leaf node of an [element].
     * @param element The element to be located in the tree.
     * @param converter A translation to turn the [element] into a multidimensional point, preferably matching the
     * multidimensional points of the tree elements.
     */
    fun <S> find(element: S, converter: (S) -> Point): T? {
        require(converter(element).size == dimension) {
            "The conversion for element $element has ${converter(
                element
            ).size} dimensions, but the Tree is $dimension dimensional"
        }
        // The elements of the tree are assigned a proximity score; The priority queue should use this score for insertion
        val comparator = Comparator<Pair<KDElement<T>, Double>> { a, b ->
            a.second.compareTo(b.second)
        }
        val queue = PriorityQueue(comparator)
        queue.add(root to root.distance(element, converter))

        // Keep the best element if a leaf has been unrolled
        var best: T? = null
        var bestMetric: Double = Double.POSITIVE_INFINITY

        while (queue.isNotEmpty()) {
            val result = queue.poll()
            // If an actual node has been found anything with a larger metric can be skipped as it will not improve the result
            if (result.second >= bestMetric) {
                continue
            } else {
                if (result.first is KDTreeLeaf) {
                    // The best result can only be updated if and only if a leaf node has been found.
                    // Intermediate nodes only provide a heuristic.
                    best = result.first.point
                    bestMetric = result.second
                } else {
                    // Only an intermediate node will have children, which need to be added to the queue.
                    queue.addAll(result.first.evaluate(element, converter))
                }
            }
        }
        return best
    }
}

private fun <T> build(converter: (T) -> Point, size: Int): ComparatorBlock<T> {
    val blocks = (0..<size).map { ComparatorBlock(it, Double::compareTo, converter) }
    blocks.zipWithNext { first, second -> first.next = second }
    blocks.last().next = blocks.first()
    return blocks.first()
}
private typealias Point = DoubleArray

private fun Point.distanceTo(other: Point): Double {
    require(this.size == other.size) { "Points must have the same dimension" }
    return kotlin.math.sqrt(this.zip(other).sumOf { (a, b) -> (a - b).pow(2) })
}

private class ComparatorBlock<T>(val index: Int, val comparator: Comparator<Double>, val converter: (T) -> Point) {

    lateinit var next: ComparatorBlock<T>
}

private fun <T> List<T>.splitByMedian(): Pair<List<T>, List<T>> {
    return subList(0, size / 2) to subList(size / 2, size)
}

private fun <T> List<T>.median(): T {
    return get(size / 2)
}

private class Hypercube(val min: Point, val max: Point) {
    override fun toString(): String {
        return "${min.joinToString { it.toString() }} ${max.joinToString { it.toString() }}"
    }

    fun closestDistance(to: Point): Double {
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

        return kotlin.math.sqrt(distance)
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
    fun <S> distance(element: S, converter: (S) -> Point): Double
    fun <S> evaluate(element: S, metric: (S) -> Point): List<Pair<KDElement<T>, Double>>
}

private class KDTreeLeaf<T>(override val point: T, val converter: (T) -> DoubleArray) : KDElement<T> {
    override fun find(element: T): T {
        return point
    }

    override fun elements(): List<T> {
        return listOf(point)
    }

    override fun <S> evaluate(element: S, metric: (S) -> Point): List<Pair<KDElement<T>, Double>> {
        return listOf()
    }

    override fun <S> distance(element: S, converter: (S) -> Point): Double {
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

    override fun <S> evaluate(element: S, metric: (S) -> Point): List<Pair<KDElement<T>, Double>> {
        return listOf(left to left.distance(element, metric), right to right.distance(element, metric))
    }

    override fun <S> distance(element: S, converter: (S) -> Point): Double {
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

    private val pivot: Point

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
