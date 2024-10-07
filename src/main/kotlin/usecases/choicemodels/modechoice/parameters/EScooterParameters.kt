package usecases.choicemodels.modechoice.parameters

import usecases.choicemodels.modechoice.Alpha
import usecases.choicemodels.modechoice.CustomAge
import usecases.choicemodels.modechoice.ModePersonScope
import usecases.choicemodels.modechoice.NumCarCostBeta
import usecases.choicemodels.modechoice.StandardActivities
import usecases.choicemodels.modechoice.StandardCars
import usecases.choicemodels.modechoice.StandardDrivingLicence
import usecases.choicemodels.modechoice.StandardGender
import usecases.choicemodels.modechoice.TravelTimeBeta
import usecases.choicemodels.modechoice.WithCost

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
object EScooterParameters : WithCost {
    override val alpha: Alpha =
        object : Alpha, StandardGender, StandardCars, StandardActivities, StandardDrivingLicence, CustomAge {
            override val drivingLicence: Double = -0.73583050105007
            override val female: Double = -0.925131277333908
            override val numberOfCars: Double = -0.291609361059158
            override val work: Double = 0.0
            override val education: Double = 0.0
            override val business: Double = 0.0
            override val leisure: Double = 0.495988304864423
            override val service: Double = -1.02218083324273 - 0.5
            override val shopping: Double = -0.87569267923033
            override val constant: Double = -6.33113485902361 + 0.9 + 0.08

            val youngAdults: Double = 1.30960251657394
            val adults: Double = 0.320663130991353
            val seniorAdults: Double = -0.392923883952931
            override fun evaluateAge(person: ModePersonScope): Double {
                return when (person.person.age) {
                    in 18..29 -> youngAdults
                    in 30..49 -> adults
                    in 50..69 -> seniorAdults
                    else -> 0.0
                }
            }
        }

    override val travelTimeBeta: TravelTimeBeta = object : TravelTimeBeta() {
        override val constant: Double = -0.104258043365681
    }
    override val travelCostBeta = NumCarCostBeta()
}
