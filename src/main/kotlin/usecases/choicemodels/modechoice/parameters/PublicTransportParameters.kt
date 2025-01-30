package usecases.choicemodels.modechoice.parameters

import domain.data.EconomicStatus
import domain.enums.Mode
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import usecases.choicemodels.modechoice.Alpha
import usecases.choicemodels.modechoice.CustomEconomicStatus
import usecases.choicemodels.modechoice.CustomNextActivity
import usecases.choicemodels.modechoice.CustomPreviousMode
import usecases.choicemodels.modechoice.ModePersonScope
import usecases.choicemodels.modechoice.StandardActivities
import usecases.choicemodels.modechoice.StandardAge
import usecases.choicemodels.modechoice.StandardCars
import usecases.choicemodels.modechoice.StandardDrivingLicence
import usecases.choicemodels.modechoice.StandardEconomicStatus
import usecases.choicemodels.modechoice.StandardGender
import usecases.choicemodels.modechoice.StandardHasCommuterTicket
import usecases.choicemodels.modechoice.StandardParkingPressure
import usecases.choicemodels.modechoice.TravelCostBeta
import usecases.choicemodels.modechoice.TravelTimeActivities
import usecases.choicemodels.modechoice.TravelTimeBeta
import usecases.choicemodels.modechoice.WithCost

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
internal class PublicTransportParameters(publicTransport: Mode, purposes: ChoiceModelPurposes) : WithCost {
    override val alpha: Alpha =
        object :
            Alpha,
            StandardEconomicStatus,
            StandardAge,
            StandardHasCommuterTicket,
            StandardCars,
            StandardActivities,
            CustomPreviousMode,
            StandardDrivingLicence,
            StandardGender,
            StandardParkingPressure {
            override val mode: Mode = publicTransport

            override val female: Double = 0.140430498472284
            override val commuterTicket: Double = 1.40430480389687
            override val numberOfCars: Double = -1.07827861934364
            override val work: Double = 1.19610336106273 + 0.55
            override val education: Double = 0.5
            override val business: Double = 1.7543148414908 - 1.1
            override val leisure: Double = 0.782269682255019 - 0.2
            override val service: Double = -0.727680174176148 - 2
            override val shopping: Double = -0.68430769614711 + 0.6
            override val drivingLicence: Double = -0.717703886473169
            override val minors: Double = 2.84820590066083 - 0.3
            override val youngAdults: Double = 0.364277898178094
            override val adults: Double = 0.0843166645310694
            override val economicStatus: Double = -0.0849228845892844 - 0.08
            override val baseParkingPressure: Double =
                0.569244759402515
            override val elasticityParkingPressure: Double =
                0.498718831912856

            override val constant: Double = -5.1223586833361 - 0.4 + 0.27
            override val purposes: ChoiceModelPurposes = purposes

            override fun evaluatePreviousMode(person: ModePersonScope): Double {
                // TODO parameter defined in function, should be moved elsewhere
                return if (person.previousMode == mode) 1.84784910323771 else 0.0
            }
        }
    override val travelTimeBeta: TravelTimeBeta = object :
        TravelTimeBeta(),
        TravelTimeActivities,
        StandardHasCommuterTicket {
        override val work: Double = -0.01921914022135 - 0.01
        override val leisure: Double = 0.0
        override val business: Double = -0.0527001295272004 + 0.04
        override val commuterTicket: Double = -0.0139162098577208
        override val constant: Double = -0.0332164435548626 + 0.01
        override val purposes: ChoiceModelPurposes = purposes
    }

    override val travelCostBeta: TravelCostBeta = object : TravelCostBeta(), CustomEconomicStatus, CustomNextActivity {
        override val constant: Double = -0.173579310377872
        override val purposes: ChoiceModelPurposes = purposes

        override fun evaluateEconomicStatus(person: ModePersonScope): Double {
            return if (person.person.household.economicStatus == EconomicStatus.VERY_HIGH) 0.029633074216897 else 0.0
        }

        override fun evaluateNextActivity(person: ModePersonScope): Double {
            return if (person.nextActivity.type == purposes.business) 0.0840900269251931 - 0.02 else 0.0
        }
    }
}
