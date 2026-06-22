package edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels
import edu.kit.ifv.domain.shared.car.CarSegment
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.parameters.CarSegmentParameters
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.parameters.SimplifiedParameters
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.kilometers

val carSegmentChoiceModel = DiscreteStructure<CarSegment, CarSegmentChoice, CarSegmentParameters> {
    option(CarSegment.SMALL) {
        0.0
    }
    option(CarSegment.MIDSIZE, parameters = { toMidsizeParameterSet() }) { _, characteristics ->
        defaultUtilityFunction(this, characteristics)
    }
    option(CarSegment.LARGE, parameters = { toLargeParameterSet() }) { _, characteristics ->
        defaultUtilityFunction(this, characteristics)
    }
}.multinomialLogit("ExampleTransitPassModel")

@Suppress("MagicNumber")
private val defaultUtilityFunction: SimplifiedParameters.(CarSegmentChoice) -> Double =
    {
        constant +
            if (it.isCommuting) {
                (it.commuterDistance in 0.kilometers..<10.kilometers) * commuterDistance0to10kilometers +
                    (it.commuterDistance in 10.kilometers..<20.kilometers) * commuterDistance10to20kilometers +
                    (it.commuterDistance in 20.kilometers..<30.kilometers) * commuterDistance20to30kilometers +
                    (it.commuterDistance in 30.kilometers..<40.kilometers) * commuterDistance30to40kilometers +
                    (it.commuterDistance in 40.kilometers..<50.kilometers) * commuterDistance40to50kilometers +
                    (it.commuterDistance >= 50.kilometers) * commuterDistanceAtLeast50kilometers
            } else {
                0.0
            } +

            (it.householdSize == 1) * householdSize1 +
            (it.householdSize == 2) * householdSize2 +
            (it.householdSize >= 3) * householdSize3orMore +

            (it.householdIncome in 0.euros..<500.euros) * incomeIn0to499 +
            (it.householdIncome in 500.euros..<1000.euros) * incomeIn500to999 +
            (it.householdIncome in 1000.euros..<1500.euros) * incomeIn1000to1499 +
            (it.householdIncome in 1500.euros..<2000.euros) * incomeIn1500to1999 +
            (it.householdIncome in 2000.euros..<2500.euros) * incomeIn2000to2499 +
            (it.householdIncome in 2500.euros..<3000.euros) * incomeIn2500to2999 +
            (it.householdIncome in 3000.euros..<3500.euros) * incomeIn3000to3499 +
            (it.householdIncome >= 3500.euros) * incomeAtLeast3500 +
            (it.householdIncome <= 0.euros) * incomeNegative +

            (it.numberOfCars >= 2) * atLeast2Cars +
            (it.sex == Sex.FEMALE) * female
    }
