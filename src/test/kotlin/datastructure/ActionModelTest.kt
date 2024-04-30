package datastructure

import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

/**
 * The [ActionModel] provides a basic implementation of a [PlanModel] using a sorted set. Tests can use this model as
 * a baseline i.e. tests should usually work with this reasonably simple model.
 */
class ActionModelTest : PlanModelTest() {

    override lateinit var model: PlanModel

    @BeforeTest
    fun setup() {
        model = ActionModel()
    }

//    @Test
//    fun remove() {
//        model.add(activity1)
//        assertEquals(model.actions(), sortedSetOf<Action>(activity1))
//        model.remove(activity1)
//        assertContentEquals(model.actions(), sortedSetOf())
//    }

    @Test
    fun view() {
//        val view = model.view()
//        model.add(activity1)
//        assertEquals(view.first(), activity1)
    }
}
