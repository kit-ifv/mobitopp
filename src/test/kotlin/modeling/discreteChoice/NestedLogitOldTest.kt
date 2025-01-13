//package modeling.discreteChoice
//
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//private enum class Attempt {
//    ONE, TWO, THREE, FOUR;
//}
//
//class NestedLogitOldTest {
//    @Test
//    fun firstTest() {
//        val nested = NestedLogitOld.root<Attempt, Unit> {
//            nest(lambda = 0.001) {
//                add(Attempt.ONE) { _, _ -> 0.0 }
//                add(Attempt.TWO) { _, _ -> 0.0 }
//
//            }
//            add(Attempt.THREE) { _, _ -> 0.0 }
//            add(Attempt.FOUR) { _, _ -> 0.0 }
//        }
//
//        val result = nested.calculateProbabilities(setOf(Attempt.ONE, Attempt.TWO, Attempt.THREE))
//        assertEquals(result.keys, setOf(Attempt.ONE, Attempt.TWO, Attempt.THREE))
//
//    }
//
//    @Test
//    fun simplestNested() {
//        val nested = NestedLogitOld.root<Attempt, Unit> {
//
//            add(Attempt.ONE) { _, _ -> 0.0 }
//            add(Attempt.TWO) { _, _ -> 0.0 }
//        }
//
//        val result = nested.calculateProbabilities(setOf(Attempt.ONE, Attempt.TWO, Attempt.THREE))
//        assertEquals(result[Attempt.ONE], 0.5)
//        assertEquals(result[Attempt.TWO], 0.5)
//    }
//
//    @Test
//    fun multinomial() {
//        val nested = NestedLogitOld.root<Attempt, Unit> {
//            nest(lambda = 1.0) {
//                add(Attempt.ONE) { _, _ -> 0.0 }
//                add(Attempt.TWO) { _, _ -> 0.0 }
//            }
//            add(Attempt.THREE) { _, _ -> 0.0 }
//        }
//
//        val result = nested.calculateProbabilities(setOf(Attempt.ONE, Attempt.TWO, Attempt.THREE))
//        assertEquals(result[Attempt.ONE], 1.0 / 3)
//        assertEquals(result[Attempt.TWO], 1.0 / 3)
//        assertEquals(result[Attempt.THREE], 1.0 / 3)
//    }
//
//    @Test
//    fun redBusBlueBus() {
//        val nested = NestedLogitOld.root<Attempt, Unit> {
//            nest(lambda = Double.MIN_VALUE) {
//                add(Attempt.ONE) { _, _ -> 0.0 }
//                add(Attempt.TWO) { _, _ -> 0.0 }
//            }
//            add(Attempt.THREE) { _, _ -> 0.0 }
//        }
//
//        val result = nested.calculateProbabilities(setOf(Attempt.ONE, Attempt.TWO, Attempt.THREE))
//        assertEquals(result[Attempt.ONE], 1.0 / 4)
//        assertEquals(result[Attempt.TWO], 1.0 / 4)
//        assertEquals(result[Attempt.THREE], 1.0 / 2)
//    }
//}