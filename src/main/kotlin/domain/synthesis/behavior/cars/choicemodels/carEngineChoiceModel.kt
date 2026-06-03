package domain.synthesis.behavior.cars.choicemodels

import domain.shared.enums.areatype.SizebasedRegiostarClassification
import domain.simulation.data.car.engine.EngineType
import domain.synthesis.behavior.cars.choicemodels.parameters.EngineParameters
import domain.synthesis.behavior.cars.choicemodels.parameters.EngineSpecificParameters
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit
import edu.kit.ifv.units.DistanceUnit

val carEngineChoiceModel = DiscreteStructure<EngineType, EngineAlternative, EngineParameters> {
    option(EngineType.COMBUSTION) {
        0.0
    }
    option(EngineType.ELECTRIC, parameters = { electicParameters() }) { _, characteristics ->
        defaultUtilityFunction(this, characteristics)
    }
    option(EngineType.HYBRID, parameters = { hybridParameters() }) { _, characteristics ->
        defaultUtilityFunction(this, characteristics)
    }
}.multinomialLogit("ExampleEngineMNL")

@Suppress("MagicNumber")
private val defaultUtilityFunction: EngineSpecificParameters.(EngineAlternative) -> Double = {
    constant +
        it.workDistance.toDouble(DistanceUnit.KILOMETERS) * workDistance +
        it.educationDistance.toDouble(DistanceUnit.KILOMETERS) * educationDistance +

        it.sex.isMale() * isMale +

        it.isWorking * fullTime +
        it.isParttime * partTime +
        it.isHomekeeper * homekeeper +
        it.isStudentTertiary * studentTertiary +
        it.isStudentSecondary * studentSecondary +
        it.isEducationEmployment * educationEmployment +
        it.isUnemployed * unemployed +
        it.isRetired * retired +

        (it.age in 18..<25) * age18to25 +
        (it.age in 25..<35) * age25to35 +
        (it.age in 35..<45) * age35to45 +
        (it.age in 45..<55) * age45to55 +
        (it.age in 55..<65) * age55to65 +
        (it.age in 65..<75) * age65to75 +
        (it.age in 75..<85) * age75to85 +

        (it.householdNumberOfCars == 1) * numPKW1 +
        (it.householdNumberOfCars == 2) * numPKW2 +
        (it.householdNumberOfCars == 3) * numPKW3 +
        (it.householdNumberOfCars == 4) * numPKW4 +

        (it.householdSize == 1) * householdSize1 +
        (it.householdSize == 2) * householdSize2 +
        (it.householdSize == 3) * householdSize3 +
        (it.householdSize == 4) * householdSize4 +

        (it.regionType == SizebasedRegiostarClassification.CITY) * regionStadt +
        (it.regionType == SizebasedRegiostarClassification.SMALL_TOWN) * regionKleinstadt +
        (it.regionType == SizebasedRegiostarClassification.URBAN_AREA) * regionStadtraum +
        (it.regionType == SizebasedRegiostarClassification.RURAL_AREA) * regionLandraum
}
