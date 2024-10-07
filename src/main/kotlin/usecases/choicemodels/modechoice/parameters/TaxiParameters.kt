package usecases.choicemodels.modechoice.parameters

import domain.enums.LegacyActivityType
import units.kilometers
import usecases.choicemodels.modechoice.Alpha
import usecases.choicemodels.modechoice.CustomAge
import usecases.choicemodels.modechoice.CustomDistance
import usecases.choicemodels.modechoice.CustomNextActivity
import usecases.choicemodels.modechoice.CustomSimulationTime
import usecases.choicemodels.modechoice.ModePersonScope
import usecases.choicemodels.modechoice.ModeZoneScope
import usecases.choicemodels.modechoice.NumCarCostBeta
import usecases.choicemodels.modechoice.StandardActivities
import usecases.choicemodels.modechoice.StandardCars
import usecases.choicemodels.modechoice.StandardDrivingLicence
import usecases.choicemodels.modechoice.StandardEconomicStatus
import usecases.choicemodels.modechoice.StandardSimulationTime
import usecases.choicemodels.modechoice.TravelCostBeta
import usecases.choicemodels.modechoice.TravelTimeBeta
import usecases.choicemodels.modechoice.WithCost
import utils.units.AbsoluteTime

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
object TaxiParameters : WithCost {
    override val alpha: Alpha = object :
        Alpha,
        StandardCars,
        CustomDistance,
        StandardSimulationTime,
        StandardActivities,
        StandardDrivingLicence,
        StandardEconomicStatus,
        CustomAge {
        override val economicStatus: Double = 0.100296146534807
        override val numberOfCars: Double = -0.816141508020774
        override val work: Double = -1.54402580548127
        override val education: Double = 0.0
        override val business: Double = 1.82041837389995 - 1.5 - 0.5 - 0.5
        override val leisure: Double = 0.0
        override val service: Double = -5.0
        override val shopping: Double = -1.97851919243732 + 0.3
        override val drivingLicence: Double = -1.24853468668945
        override val constant: Double = -6.26874836363272 + 1

        /**
         * Parameter influencing the Taxi Utility of young adults based on an age categorization defined by [evaluateAge]
         */
        val youngAdults = -0.458010316471613

        /**
         * Parameter used to reward (positive number) or penalize (negative number) a short distance trip by Taxi
         * defined by [evaluateDistance]
         */
        val shortDistance = -2.0
        override val evening = 0.0
        override val beforeMidnight = 0.0
        override val afterMidnight = 0.0
        override val fridaysBeforeMidnight = 1.0
        override val fridaysAfterMidnight = 2.0
        override val saturdaysBeforeMidnight = 1.0
        override val saturdaysAfterMidnight = 2.0
        override val sundaysAfterMidnight = 2.0

        /**
         * An Age in [18, 29] is considered young Adult. Every other age is not considered.
         *
         * @param age the target age
         * @return [youngAdults]-Parameter if the age is considered young adult, else 0.0
         */
        override fun evaluateAge(person: ModePersonScope): Double {
            return when (person.person.age) {
                in 18..29 -> youngAdults
                else -> 0.0
            }
        }

        /**
         * Distances in the interval (0.km, 1.km] are defined as short distance.
         * Every other distance is not a short distance.
         *
         * @param distance target distance to evaluate
         * @return [shortDistance]-Parameter if the distance is considered short distance, else 0.0
         */
        override fun evaluateDistance(scope: ModeZoneScope): Double {
            return when (scope.distance) {
                0.kilometers -> 0.0
                in 0.kilometers..1.kilometers -> shortDistance

                else -> 0.0
            }
        }
    }

    private fun AbsoluteTime.isBeforeMidnight(): Boolean {
        return hour >= 21
    }

    private fun AbsoluteTime.isAfterMidnight(): Boolean {
        return hour < 5
    }

    override val travelTimeBeta: TravelTimeBeta = object : TravelTimeBeta(), CustomNextActivity {
        override val constant: Double = -0.0337950092387434
        val business = -0.0165983651233786
        override fun evaluateNextActivity(person: ModePersonScope): Double {
            return if (person.nextActivity.type == LegacyActivityType.BUSINESS) business else 0.0
        }
    }

    override val travelCostBeta: TravelCostBeta = object : NumCarCostBeta(), CustomSimulationTime {
        val b_taxi_on_cost = 0.02
        override val constant: Double = super.constant + b_taxi_on_cost

        // TODO in the original utility function this is called evening, but only influences night parameters
        val b_evening_on_cost: Double = 0.173579310377872 / 3
        override fun evaluateTime(scope: ModeZoneScope): Double {
            val time = scope.time
            return if (time.isAfterMidnight() || time.isBeforeMidnight()) b_evening_on_cost else 0.0
        }
    }
}
