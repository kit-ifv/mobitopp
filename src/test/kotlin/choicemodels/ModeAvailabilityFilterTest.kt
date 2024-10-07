package choicemodels

import domain.enums.StandardMode
import org.junit.jupiter.api.Test
import testPerson
import usecases.choicemodels.ModeAvailabilityFilter

class ModeAvailabilityFilterTest {

    @Test
    fun filter() {
        val agent = testPerson

        val filter = ModeAvailabilityFilter(StandardMode)
        val results = filter.filter(listOf(StandardMode.CAR, StandardMode.BIKE), agent)
        println(results)
    }
}
