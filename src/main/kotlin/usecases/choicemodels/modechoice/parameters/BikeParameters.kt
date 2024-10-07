package usecases.choicemodels.modechoice.parameters

import domain.enums.StandardMode
import usecases.choicemodels.modechoice.Alpha
import usecases.choicemodels.modechoice.CustomPreviousMode
import usecases.choicemodels.modechoice.ModePersonScope
import usecases.choicemodels.modechoice.NoCost
import usecases.choicemodels.modechoice.StandardActivities
import usecases.choicemodels.modechoice.StandardAge
import usecases.choicemodels.modechoice.StandardCars
import usecases.choicemodels.modechoice.StandardHasCommuterTicket
import usecases.choicemodels.modechoice.TravelTimeBeta

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
object BikeParameters : NoCost {
    override val alpha: Alpha =
        object :
            Alpha,
            StandardActivities,
            StandardAge,
            CustomPreviousMode,
            StandardHasCommuterTicket,
            StandardCars {
            override val work: Double = 1.7477080826499 + 1
            override val education: Double = 1.3
            override val business: Double = 1.27524641844584 - 2
            override val leisure: Double = 0.857464481463703 + 0.6
            override val service: Double = -1.71732162997153
            override val shopping: Double = -0.00714067693903813 + 0.15
            override val numberOfCars: Double = -0.854549090872297
            override val constant: Double = -3.34401676623963 - 0.5 - 0.05 - 0.2
            override val minors: Double = 2.78514456752499
            override val youngAdults: Double = 0.389316612655132
            override val adults: Double = 0.113391623780271
            override val commuterTicket: Double = -0.138496699678098
            val previousIsBike = 3.3046316124107 - 1

            // TODO build generic previous mode interface and assign the type to this mode
            override fun evaluatePreviousMode(person: ModePersonScope): Double {
                return if (person.previousMode == StandardMode.BIKE) previousIsBike else 0.0
            }
        }
    override val travelTimeBeta: TravelTimeBeta = object : TravelTimeBeta(), StandardActivities {
        override val work: Double = -0.0403054361662421 + 0.02
        override val education: Double = 0.0
        override val leisure: Double = 0.0
        override val service: Double = 0.0
        override val shopping: Double = 0.0
        override val business: Double = -0.0448785360310769 - 0.02
        override val constant: Double = -0.132222310507272 - 0.05
    }
}
