package usecases.choicemodels.modechoice.parameters

import domain.enums.Mode
import usecases.choicemodels.modechoice.Alpha
import usecases.choicemodels.modechoice.NumCarCostBeta
import usecases.choicemodels.modechoice.TravelTimeBeta
import usecases.choicemodels.modechoice.WithCost

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
internal class CarsharingStationParameters(private val car: Mode) : WithCost {
    val carParameters = CarParameters(car)

    override val alpha = object : Alpha {
        override val constant: Double = -7.79427402396165 + 1
    }

    override val travelTimeBeta = object : TravelTimeBeta() {

        override val constant: Double = carParameters.travelTimeBeta.constant
    }

    override val travelCostBeta = NumCarCostBeta()
}
