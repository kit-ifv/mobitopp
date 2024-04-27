package datastructure


import kotlin.test.Test
import kotlin.test.assertEquals

class BlockModelTest: PlanModelTest() {
    override var model: PlanModel = BlockModel()
    @Test
    fun view() {
        val model =  BlockModel()
        val actionModel =  ActionModel(model)
        val trips = model.view()
        trips[0].overwrite {

        }
        assertEquals(trips.size, 0)

        trips.add(leg1)
        assertEquals(trips.size, 1)
        assertEquals(actionModel.actions(), setOf(leg1))
    }
//
//    @Test
//    fun viewOnTripFirst() {
//        val model =  BlockModel()
//        val view = model.view()
//
//        model.add(leg2)
//        assertEquals(view.size, 1)
//        view[0].overwrite {
//
//        }
//        model.add(leg1)
//        assertEquals(view.size, 2)
//
//        model.add(activity1)
//        model.add(activity2)
//        model.add(activity3)
//        view[0].overwrite {
//
//        }
//        assertTrue(view.isStrictlySorted())
//
//        assertEquals(view.size, 1)
//    }
}