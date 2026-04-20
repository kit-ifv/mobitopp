package core.datastructure.kdtree

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.pow
import kotlin.math.sqrt

class ReadOnlyKDTreeTest {

    private data class TestElement(
        val x: Int,
        val y: Int,
        val z: Int,
    ) {

        fun distanceTo(point: DoubleArray): Double {
            require(point.size == 3) {
                "Can only compare against 3-D Points"
            }
            return sqrt((point[0] - x).pow(2) + (point[1] - y).pow(2) + (point[2] - z).pow(2))
        }

        companion object {
            /**
             * Generate a cube grid of Elements in 3D
             * @param bound the dimensions of the cube
             */

            fun generateGrid(bound: IntRange = 0..5): List<TestElement> {
                return bound.flatMap { x ->
                    bound.flatMap { y ->
                        bound.map { z ->
                            TestElement(x, y, z)
                        }
                    }
                }
            }

            fun generateTestPoints(bound: IntRange = 0..5, steps: Int = 4): List<DoubleArray> {
                val newRange = bound.first..bound.last * steps
                return newRange.flatMap { x ->
                    newRange.flatMap { y ->
                        newRange.map { z ->
                            doubleArrayOf(x.toDouble() / steps, y.toDouble() / steps, z.toDouble() / steps)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun find() {
        val points = TestElement.generateGrid()
        val tree = ReadOnlyKDTree(points, { it.x.toDouble() }, { it.y.toDouble() }, { it.z.toDouble() })
        Assertions.assertEquals(tree.dimension, 3)
        val testPoints = TestElement.generateTestPoints()
        testPoints.forEach {
            Assertions.assertTrue(
                tree.nearestNeighbor(it) in points.groupBy { point -> point.distanceTo(it) }
                    .minBy { m -> m.key }.value
            )
        }
    }

    @Test
    fun invokeWithDimensionMismatch() {
        val points = TestElement.generateGrid()
        val tree = ReadOnlyKDTree(points, { it.x.toDouble() })
        assertThrows<IllegalArgumentException> {
            tree.nearestNeighbor(doubleArrayOf(2.0, 2.0))
        }
    }

    @Test
    fun findClosestList() {
        val points = TestElement.generateGrid()
        val tree = ReadOnlyKDTree(points, { it.x.toDouble() }, { it.y.toDouble() }, { it.z.toDouble() })
        val output = tree.findUntil((doubleArrayOf(2.0, 2.0, 1.5))).dropWhile { (_, value) -> value < 0.9 }
            .takeWhile { (_, value) -> value <= 1.5 }.toList()
        println(output)
    }
}
