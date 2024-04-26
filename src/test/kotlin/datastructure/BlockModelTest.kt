package datastructure

import kotlin.test.Test
import kotlin.test.assertEquals

class BlockModelTest: ExamplePlan() {
    @Test
    fun view() {
        val model =  BlockModel()
        val view = model.view()
        model.add(activity1)
        model.add(activity2)
        model.add(leg1)
        model.add(leg1b)

        assertEquals(view.size, 1)




    }
}