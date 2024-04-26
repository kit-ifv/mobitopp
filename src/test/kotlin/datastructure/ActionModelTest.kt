package datastructure

import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class ActionModelTest: ExamplePlan() {

    private lateinit var model: ActionModel

    @BeforeTest
    fun setup() {
        model = ActionModel()
    }
    @Test
    fun add() {
        model.add(activity1)
        assertEquals(model.actions, sortedSetOf<Action>(activity1))
    }

    @Test
    fun testAdd() {
    }

    @Test
    fun remove() {
    }

    @Test
    fun testRemove() {
    }

    @Test
    fun replaceActivities() {
    }

    @Test
    fun replaceLegs() {
    }

    @Test
    fun view() {
        val view = model.view()
        model.add(activity1)
        assertEquals(view.first(), activity1)
    }
}