package usecases.choicemodels.modechoice.parameters

import domain.enums.LegacyActivityType
import usecases.choicemodels.modechoice.Alpha
import usecases.choicemodels.modechoice.CustomAge
import usecases.choicemodels.modechoice.CustomNextActivity
import usecases.choicemodels.modechoice.ModePersonScope
import usecases.choicemodels.modechoice.NumCarCostBeta
import usecases.choicemodels.modechoice.StandardActivities
import usecases.choicemodels.modechoice.StandardCars
import usecases.choicemodels.modechoice.StandardGender
import usecases.choicemodels.modechoice.StandardParkingPressure
import usecases.choicemodels.modechoice.TravelTimeBeta
import usecases.choicemodels.modechoice.WithCost

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
internal object CarsharingFreeFloatingParameters : WithCost {
    override val alpha: Alpha =
        object : Alpha, StandardCars, StandardGender, CustomAge, StandardActivities, StandardParkingPressure {
            override val numberOfCars: Double = -0.93467367132673
            override val female: Double = -0.282109812615457
            override val work: Double = -3.0
            override val education: Double = 0.0
            override val business: Double = -0.5
            override val leisure: Double = 1.0
            override val service: Double = -0.3
            override val shopping: Double = 0.0

            // TODO another free floating utility parameter without explanation
            val b_cs_ff_mitgl_on_cs_ff = 2.12500983359484
            override val baseParkingPressure: Double = 0.99381505624896
            override val elasticityParkingPressure: Double = 0.422310707928408
            override val constant: Double = -8.69976053689462 - 2.1 - 0.1 + b_cs_ff_mitgl_on_cs_ff

            val youngAdults = 0.231608297097211
            val seniorAdults = -0.167441511452748

            override fun evaluateAge(person: ModePersonScope): Double {
                return when (person.person.age) {
                    in 18..29 -> youngAdults
                    in 50..69 -> seniorAdults
                    else -> 0.0
                }
            }
        }
    override val travelTimeBeta: TravelTimeBeta = object : TravelTimeBeta(), CustomNextActivity {
        override val constant: Double = -0.0397735554726612 - 0.02 // TODO shared parameter "b_tt_pkw
        val leisure = 0.0346206400712123
        override fun evaluateNextActivity(person: ModePersonScope): Double {
            return when (person.nextActivity.type) {
                LegacyActivityType.LEISURE,
                LegacyActivityType.PRIVATE_VISIT,
                LegacyActivityType.LEISURE_INDOOR,
                LegacyActivityType.LEISURE_OUTDOOR,
                LegacyActivityType.LEISURE_OTHER,
                LegacyActivityType.LEISURE_SIGHTSEEING,
                LegacyActivityType.LEISURE_WALK -> leisure

                else -> 0.0
            }
        }
    }
    override val travelCostBeta = NumCarCostBeta()
}
