package usecases.choicemodels.modechoice.parameters

import units.kilometers
import units.meters
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import usecases.choicemodels.modechoice.Alpha
import usecases.choicemodels.modechoice.CustomAge
import usecases.choicemodels.modechoice.CustomDistance
import usecases.choicemodels.modechoice.CustomNextActivity
import usecases.choicemodels.modechoice.CustomSimulationTime
import usecases.choicemodels.modechoice.EconomicStatusCostBeta
import usecases.choicemodels.modechoice.ModePersonScope
import usecases.choicemodels.modechoice.ModeZoneScope
import usecases.choicemodels.modechoice.StandardCars
import usecases.choicemodels.modechoice.StandardDrivingLicence
import usecases.choicemodels.modechoice.StandardEconomicStatus
import usecases.choicemodels.modechoice.StandardSimulationTime
import usecases.choicemodels.modechoice.TravelCostBeta
import usecases.choicemodels.modechoice.TravelTimeBeta
import usecases.choicemodels.modechoice.WithCost
import utils.units.AbsoluteTime

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
internal class MoiaParameters(purposes: ChoiceModelPurposes) : WithCost {
    private fun AbsoluteTime.isEvening(): Boolean {
        return hour in 17..<21
    }

    private fun AbsoluteTime.isBeforeMidnight(): Boolean {
        return hour >= 21
    }

    private fun AbsoluteTime.isAfterMidnight(): Boolean {
        return hour < 5
    }

    // TODO Moia Membership
    override val alpha: Alpha = object :
        Alpha,
        StandardCars,
        CustomNextActivity,
        StandardSimulationTime,
        CustomDistance,
        StandardDrivingLicence,
        CustomAge,
        StandardEconomicStatus {
        override val economicStatus: Double = -0.0747033564230432
        override val numberOfCars: Double = -0.522964009216794
        override val drivingLicence: Double = -0.995775183512996

        // TODO Why is this "b_konto_moia_on_moia" existing in the original utility function
        val randomParameterWithoutImpact = 0.952788891671551
        override val constant: Double = -4.98883914602145 - 0.6 + randomParameterWithoutImpact
        override val purposes: ChoiceModelPurposes = purposes

        val adults: Double = -0.133870668781963
        val seniorAdults = 0.240651982840271
        val seniors = 0.839643293408915
        override fun evaluateAge(person: ModePersonScope): Double {
            return when (person.person.age) {
                in 30..49 -> adults
                in 50..69 -> seniorAdults
                in 70..Int.MAX_VALUE -> seniors
                else -> 0.0
            }
        }

        val work: Double = 0.296408626063083 - 0.3
        val business: Double = 1.19739398327784 - 0.6
        val service: Double = 0.280743524700044 - 2
        val education: Double = 0.0
        val shopping: Double = -1.0
        val leisure: Double = 1.58945260978742
        val leisureTravel: Double = 6.0
        val businessTravel: Double = 6.0
        override fun evaluateNextActivity(person: ModePersonScope): Double {
            return when (person.nextActivity.type) {
                purposes.work -> work

                purposes.business -> business

                purposes.service -> service

                in purposes.educationTypes -> education

                in purposes.shoppingTypes -> shopping
                in purposes.leisureTypes -> leisure

                // Note that in the original transmove implementation the leisureTravel took both leisure and leisureTravel Parameters
                purposes.leisureTravel -> leisureTravel
                purposes.businessTravel -> businessTravel

                else -> 0.0
            }
        }

        val withinOneKilometer = -3.0
        val betweenOneAndTwoKilometers = -1.0
        override fun evaluateDistance(scope: ModeZoneScope): Double {
            return when (scope.distance) {
                // This ZERO case is here to ensure that the intervals are (0, 1], (1, 2] because for
                // some obscure reason the original utility function compares with < instead of <=
                0.meters -> 0.0
                in 0.kilometers..1.kilometers -> withinOneKilometer
                in 1.kilometers..2.kilometers -> betweenOneAndTwoKilometers
                else -> 0.0
            }
        }

        override val evening = 0.4
        override val beforeMidnight = 0.5
        override val afterMidnight = 0.5
        override val fridaysBeforeMidnight = 0.4 * 1.4
        override val fridaysAfterMidnight = 1.3 * 1.2
        override val saturdaysBeforeMidnight = 0.7 * 1.2
        override val saturdaysAfterMidnight = 2 * 1.5
        override val sundaysAfterMidnight = 5.0
    }
    override val travelTimeBeta: TravelTimeBeta = object : TravelTimeBeta(), CustomNextActivity {
        override val constant: Double = -0.0558270784857267 + 0.019
        override val purposes: ChoiceModelPurposes = purposes

        override fun evaluateNextActivity(person: ModePersonScope): Double {
            return if (person.nextActivity.type == purposes.business) -0.0165983651233786 else 0.0
        }
    }

    // TODO Moia Membership
    override val travelCostBeta: TravelCostBeta = object : EconomicStatusCostBeta(purposes), CustomSimulationTime {

        val eveningCost: Double = 0.173579310377872 / 3
        val nightCost = 0.173579310377872 / 2
        override fun evaluateTime(scope: ModeZoneScope): Double {
            val time = scope.time
            return when {
                time.isEvening() -> eveningCost
                time.isBeforeMidnight() || time.isAfterMidnight() -> nightCost
                else -> 0.0
            }
        }
    }
}
