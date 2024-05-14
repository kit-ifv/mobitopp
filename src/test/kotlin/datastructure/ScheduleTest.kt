package datastructure

import kotlin.test.BeforeTest

class ScheduleTest {
    private lateinit var schedule: Schedule

    @BeforeTest
    fun setup() {
        schedule = Schedule(BlockModel())
    }
}
