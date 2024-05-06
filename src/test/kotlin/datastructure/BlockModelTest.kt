package datastructure

import OTHER
import START
import utils.collections.isStrictlySorted
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class BlockModelTest : PlanModelTest() {
    private lateinit var bModel: BlockModel
    override lateinit var model: PlanModel

    @BeforeTest
    fun setup() {
        bModel = BlockModel()
        model = bModel
    }

//
    @Test
    fun noInconsistencies() {
        model.add(activity1)
        model.add(Leg.fromDuration(activity1.endTime - 1.minutes, duration = 1.hours, START, OTHER))
        assertContentEquals(model.actions(), setOf<Action>(activity1))
        model.add(activity2)
        model.add(activity3)
        // Cannot assert consistency, the locations don't match
        assertTrue(model.actions().isStrictlySorted())

        model.add(leg1)
        assertContentEquals(model.actions(), sortedSetOf(activity1, activity2, activity3, leg1))
    }
}
