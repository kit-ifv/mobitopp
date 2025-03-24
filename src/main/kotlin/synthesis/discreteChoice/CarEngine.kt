package synthesis.discreteChoice

import domain.data.Employment
import domain.data.EngineType
import domain.data.Sex
import domain.enums.Regiostar17
import domain.enums.SizebasedRegiostarClassification
import modeling.discreteChoice.AllocatedLogit
import modeling.discreteChoice.ChoiceSituation
import modeling.discreteChoice.KnownDiscreteChoiceModel
import modeling.discreteChoice.times
import synthesis.SurveyWithCommute
import synthesis.domain.SynthesisHousehold
import synthesis.domain.SynthesisPerson
import units.Distance
import units.DistanceUnit

val FatParameters = EngineParameters(
    CONST_BEV = -9.8079,
    WORKDIS_BEV = 0.0525,
    AUSBDIS_BEV = 0.0249,
    SEX_MALE_BEV = -0.031,
    JOB_FULLTIME_BEV = 0.2378,
    JOB_PARTTIME_BEV = -0.0814,
    JOBLESS_BEV = -0.0755,
    STUDENT_TERTIARY_BEV = -1.0111,
    STUDENT_SECONDARY_BEV = 0.0151,
    EDUCATION_BEV = -0.1837,
    UNEMPLOYED_BEV = -0.1599,
    RETIRED_BEV = -0.218,
    AGE_18_TO_25_BEV = -0.125,
    AGE_25_TO_35_BEV = 0.2121,
    AGE_35_TO_45_BEV = 0.3012,
    AGE_45_TO_55_BEV = 0.3612,
    AGE_55_TO_65_BEV = 0.3242,
    AGE_65_TO_75_BEV = 0.3948,
    AGE_75_TO_85_BEV = -0.3124,
    ANZ_PKW_1_BEV = 0.8749,
    ANZ_PKW_2_BEV = 0.6476,
    ANZ_PKW_3_BEV = 0.8238,
    ANZ_PKW_4_BEV = 0.7095,
    HHGRO_1_BEV = 0.2985,
    HHGRO_2_BEV = -0.1356,
    HHGRO_3_BEV = -0.4157,
    HHGRO_4_BEV = -0.1025,
    STADT_BEV = -9.5,
    KLEINSTADT_BEV = -3.0,
    STADTRAUM_BEV = -0.7000000000000001,
    LANDRAUM_BEV = 0.0,
    CONST_EREV = -10.3079,
    WORKDIS_EREV = 0.0525,
    AUSBDIS_EREV = 0.0249,
    SEX_MALE_EREV = -0.031,
    JOB_FULLTIME_EREV = 0.2378,
    JOB_PARTTIME_EREV = -0.0814,
    JOBLESS_EREV = -0.0755,
    STUDENT_TERTIARY_EREV = -1.0111,
    STUDENT_SECONDARY_EREV = 0.0151,
    EDUCATION_EREV = -0.1837,
    UNEMPLOYED_EREV = -0.1599,
    RETIRED_EREV = -0.218,
    AGE_18_TO_25_EREV = -0.125,
    AGE_25_TO_35_EREV = 0.2121,
    AGE_35_TO_45_EREV = 0.3012,
    AGE_45_TO_55_EREV = 0.3612,
    AGE_55_TO_65_EREV = 0.3242,
    AGE_65_TO_75_EREV = 0.3948,
    AGE_75_TO_85_EREV = -0.3124,
    ANZ_PKW_1_EREV = 0.8749,
    ANZ_PKW_2_EREV = 0.6476,
    ANZ_PKW_3_EREV = 0.8238,
    ANZ_PKW_4_EREV = 0.7095,
    HHGRO_1_EREV = 0.2985,
    HHGRO_2_EREV = -0.1356,
    HHGRO_3_EREV = -0.4157,
    HHGRO_4_EREV = -0.1025,
    STADT_EREV = -9.5,
    KLEINSTADT_EREV = -3.0,
    STADTRAUM_EREV = -0.7000000000000001,
    LANDRAUM_EREV = 0.0
)

@Suppress(
    "MagicNumber",
    "ConstructorParameterNaming"
) // Parameters for configurations are magic, but there is no better representation
data class EngineParameters(
    val CONST_BEV: Double = -2.3079 - 7.5,
    val WORKDIS_BEV: Double = 0.0525,
    val AUSBDIS_BEV: Double = 0.0249,
    val SEX_MALE_BEV: Double = -0.0310,
    val JOB_FULLTIME_BEV: Double = 0.2378,
    val JOB_PARTTIME_BEV: Double = -0.0814,
    val JOBLESS_BEV: Double = -0.0755,
    val STUDENT_TERTIARY_BEV: Double = -1.0111,
    val STUDENT_SECONDARY_BEV: Double = 0.0151,
    val EDUCATION_BEV: Double = -0.1837,
    val UNEMPLOYED_BEV: Double = -0.1599,
    val RETIRED_BEV: Double = -0.2180,
    val AGE_18_TO_25_BEV: Double = -0.1250,
    val AGE_25_TO_35_BEV: Double = 0.2121,
    val AGE_35_TO_45_BEV: Double = 0.3012,
    val AGE_45_TO_55_BEV: Double = 0.3612,
    val AGE_55_TO_65_BEV: Double = 0.3242,
    val AGE_65_TO_75_BEV: Double = 0.3948,
    val AGE_75_TO_85_BEV: Double = -0.3124,
    val ANZ_PKW_1_BEV: Double = 0.8749,
    val ANZ_PKW_2_BEV: Double = 0.6476,
    val ANZ_PKW_3_BEV: Double = 0.8238,
    val ANZ_PKW_4_BEV: Double = 0.7095,
    val HHGRO_1_BEV: Double = 0.2985,
    val HHGRO_2_BEV: Double = -0.1356,
    val HHGRO_3_BEV: Double = -0.4157,
    val HHGRO_4_BEV: Double = -0.1025,
    val STADT_BEV: Double = -3 - 6.5,
    val KLEINSTADT_BEV: Double = -1.0 - 2.0,
    val STADTRAUM_BEV: Double = 0.1 - 0.8,
    val LANDRAUM_BEV: Double = 0.0,
    val CONST_EREV: Double = -2.3079 - 8,
    val WORKDIS_EREV: Double = 0.0525,
    val AUSBDIS_EREV: Double = 0.0249,
    val SEX_MALE_EREV: Double = -0.0310,
    val JOB_FULLTIME_EREV: Double = 0.2378,
    val JOB_PARTTIME_EREV: Double = -0.0814,
    val JOBLESS_EREV: Double = -0.0755,
    val STUDENT_TERTIARY_EREV: Double = -1.0111,
    val STUDENT_SECONDARY_EREV: Double = 0.0151,
    val EDUCATION_EREV: Double = -0.1837,
    val UNEMPLOYED_EREV: Double = -0.1599,
    val RETIRED_EREV: Double = -0.2180,
    val AGE_18_TO_25_EREV: Double = -0.1250,
    val AGE_25_TO_35_EREV: Double = 0.2121,
    val AGE_35_TO_45_EREV: Double = 0.3012,
    val AGE_45_TO_55_EREV: Double = 0.3612,
    val AGE_55_TO_65_EREV: Double = 0.3242,
    val AGE_65_TO_75_EREV: Double = 0.3948,
    val AGE_75_TO_85_EREV: Double = -0.3124,
    val ANZ_PKW_1_EREV: Double = 0.8749,
    val ANZ_PKW_2_EREV: Double = 0.6476,
    val ANZ_PKW_3_EREV: Double = 0.8238,
    val ANZ_PKW_4_EREV: Double = 0.7095,
    val HHGRO_1_EREV: Double = 0.2985,
    val HHGRO_2_EREV: Double = -0.1356,
    val HHGRO_3_EREV: Double = -0.4157,
    val HHGRO_4_EREV: Double = -0.1025,
    val STADT_EREV: Double = -3 - 6.5,
    val KLEINSTADT_EREV: Double = -1.0 - 2.0,
    val STADTRAUM_EREV: Double = 0.1 - 0.8,
    val LANDRAUM_EREV: Double = 0.0,
) {
    fun electicParameters(): EngineSpecificParameters {
        return EngineSpecificParameters(
            CONST_BEV,
            WORKDIS_BEV,
            AUSBDIS_BEV,
            SEX_MALE_BEV,
            JOB_FULLTIME_BEV,
            JOB_PARTTIME_BEV,
            JOBLESS_BEV,
            STUDENT_TERTIARY_BEV,
            STUDENT_SECONDARY_BEV,
            EDUCATION_BEV,
            UNEMPLOYED_BEV,
            RETIRED_BEV,
            AGE_18_TO_25_BEV,
            AGE_25_TO_35_BEV,
            AGE_35_TO_45_BEV,
            AGE_45_TO_55_BEV,
            AGE_55_TO_65_BEV,
            AGE_65_TO_75_BEV,
            AGE_75_TO_85_BEV,
            ANZ_PKW_1_BEV,
            ANZ_PKW_2_BEV,
            ANZ_PKW_3_BEV,
            ANZ_PKW_4_BEV,
            HHGRO_1_BEV,
            HHGRO_2_BEV,
            HHGRO_3_BEV,
            HHGRO_4_BEV,
            STADT_BEV,
            KLEINSTADT_BEV,
            STADTRAUM_BEV,
            LANDRAUM_BEV,
        )
    }

    fun hybridParameters(): EngineSpecificParameters {
        return EngineSpecificParameters(
            CONST_EREV,
            WORKDIS_EREV,
            AUSBDIS_EREV,
            SEX_MALE_EREV,
            JOB_FULLTIME_EREV,
            JOB_PARTTIME_EREV,
            JOBLESS_EREV,
            STUDENT_TERTIARY_EREV,
            STUDENT_SECONDARY_EREV,
            EDUCATION_EREV,
            UNEMPLOYED_EREV,
            RETIRED_EREV,
            AGE_18_TO_25_EREV,
            AGE_25_TO_35_EREV,
            AGE_35_TO_45_EREV,
            AGE_45_TO_55_EREV,
            AGE_55_TO_65_EREV,
            AGE_65_TO_75_EREV,
            AGE_75_TO_85_EREV,
            ANZ_PKW_1_EREV,
            ANZ_PKW_2_EREV,
            ANZ_PKW_3_EREV,
            ANZ_PKW_4_EREV,
            HHGRO_1_EREV,
            HHGRO_2_EREV,
            HHGRO_3_EREV,
            HHGRO_4_EREV,
            STADT_EREV,
            KLEINSTADT_EREV,
            STADTRAUM_EREV,
            LANDRAUM_EREV,
        )
    }
}

data class EngineSpecificParameters(
    val constant: Double,
    val workDistance: Double,
    val educationDistance: Double,
    val isMale: Double,
    val fullTime: Double,
    val partTime: Double,
    val homekeeper: Double,
    val studentTertiary: Double,
    val studentSecondary: Double,
    val educationEmployment: Double,
    val unemployed: Double,
    val retired: Double,
    val age18to25: Double, // 18..25
    val age25to35: Double,
    val age35to45: Double,
    val age45to55: Double,
    val age55to65: Double,
    val age65to75: Double,
    val age75to85: Double,
    val numPKW1: Double,
    val numPKW2: Double,
    val numPKW3: Double,
    val numPKW4: Double,
    val householdSize1: Double,
    val householdSize2: Double,
    val householdSize3: Double,
    val householdSize4: Double,
    val regionStadt: Double,
    val regionKleinstadt: Double,
    val regionStadtraum: Double,
    val regionLandraum: Double

)

class EngineSituation(
    override val choice: EngineType,
    person: SurveyWithCommute,
    household: SynthesisHousehold<out SurveyWithCommute>
) : ChoiceSituation<EngineType>() {
    val workDistance: Distance = person.distanceWork // Distance to pole zone
    val educationDistance: Distance = person.distanceEducation
    val sex: Sex = person.sex
    val employment: Employment = person.employment
    val age: Int = person.age
    val householdNumberOfCars: Int = household.amountOfCars
    val householdSize: Int = household.size
    val regionTypeRegiostar17: Regiostar17 = household.location.zone?.regionType?.toRegiostar17()
        ?: throw NoSuchElementException("${household.location} zone does not have a proper regiostar type")
    val regionType = regionTypeRegiostar17.toSizebasedClassification()

    val isWorking = employment == Employment.FULLTIME
    val isParttime = employment == Employment.PARTTIME
    val isHomekeeper = employment == Employment.HOMEKEEPER
    val isStudentTertiary = employment == Employment.STUDENT_TERTIARY
    val isStudentSecondary = employment == Employment.STUDENT_SECONDARY
    val isEducationEmployment = employment == Employment.EDUCATION
    val isUnemployed = employment == Employment.UNEMPLOYED
    val isRetired = employment == Employment.RETIRED
}

fun EngineType.toChoice(
    person: SynthesisPerson<out SurveyWithCommute>,
    household: SynthesisHousehold<out SurveyWithCommute>
): EngineSituation {
    return EngineSituation(this, person.info, household)
}

val carEngineChoiceModel = KnownDiscreteChoiceModel<EngineType, EngineSituation, EngineParameters>(
    AllocatedLogit.create {
        option(EngineType.COMBUSTION) {
            0.0
        }
        option(EngineType.ELECTRIC, parameters = { electicParameters() }) {
            defaultUtilityFunction(this, it)
        }
        option(EngineType.HYBRID, parameters = { hybridParameters() }) {
            defaultUtilityFunction(this, it)
        }
    }
)

@Suppress("MagicNumber")
private val defaultUtilityFunction: EngineSpecificParameters.(EngineSituation) -> Double = {
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
