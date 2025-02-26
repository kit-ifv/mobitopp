package usecases.choicemodels.modechoice.parameters

import domain.data.EconomicStatus
import domain.enums.Mode
import units.kilometers
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import usecases.choicemodels.modechoice.Alpha
import usecases.choicemodels.modechoice.CustomDistance
import usecases.choicemodels.modechoice.CustomEconomicStatus
import usecases.choicemodels.modechoice.CustomPreviousMode
import usecases.choicemodels.modechoice.ModePersonScope
import usecases.choicemodels.modechoice.ModeZoneScope
import usecases.choicemodels.modechoice.StandardActivities
import usecases.choicemodels.modechoice.StandardEconomicStatus
import usecases.choicemodels.modechoice.StandardGender
import usecases.choicemodels.modechoice.StandardHasCommuterTicket
import usecases.choicemodels.modechoice.TravelCostBeta
import usecases.choicemodels.modechoice.TravelTimeBeta
import usecases.choicemodels.modechoice.WithCost

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
internal class CarParameters(car: Mode, purposes: ChoiceModelPurposes) : WithCost {
    override val alpha: Alpha =
        object :
            Alpha,
            CustomDistance,
            CustomPreviousMode,
            StandardEconomicStatus,
            StandardActivities,
            StandardHasCommuterTicket,
            StandardGender {
            override val mode: Mode = car

            override val work: Double = -0.9
            override val education: Double = -2.0
            override val business: Double = 0.0
            override val leisure: Double = -0.135539830154819 - 0.3
            override val service: Double = 0.9
            override val shopping: Double = 0.484848310471716 - 0.3

            val accessEgress = -0.0886209594184093

            // TODO move access egress out of constant
            override val constant: Double = -4.69347301935537 + 1.7 + 0.1 + 0.1 + (accessEgress * 3)
            override val purposes: ChoiceModelPurposes = purposes

            override val female: Double = -0.0530617111718515
            override val commuterTicket = -0.42095211929704
            override val economicStatus = -0.0925723881400436 - 0.1

            val closeDistance: Double = -1.0

            val previousMode = 2.39983908693686

            override fun evaluatePreviousMode(person: ModePersonScope): Double {
                return if (person.previousMode == mode) previousMode else 0.0
            }

            override fun evaluateDistance(scope: ModeZoneScope): Double {
                return if ((scope.distance <= 1.0.kilometers) && (scope.distance > 0.0.kilometers)) {
                    closeDistance
                } else {
                    0.0
                }
            }
        }

    override val travelTimeBeta: TravelTimeBeta = object : TravelTimeBeta(), StandardActivities {
        override val work: Double = 0.0198554464250517 + 0.01
        override val education: Double = 0.0
        override val leisure: Double = 0.0224560635046835
        override val service: Double = 0.0
        override val shopping: Double = 0.0
        override val business: Double = 0.0
        override val constant: Double = -0.0397735554726612 - 0.02
        override val purposes: ChoiceModelPurposes = purposes
    }
    override val travelCostBeta: TravelCostBeta = object : TravelCostBeta(), CustomEconomicStatus {
        val economicStatus: Double = 0.0276716356674532
        override val constant: Double = -0.0681515897245829 - 0.03
        override val purposes: ChoiceModelPurposes = purposes

        override fun evaluateEconomicStatus(person: ModePersonScope): Double {
            return if (person.person.household.economicStatus == EconomicStatus.VERY_HIGH) this.economicStatus else 0.0
        }
    }
}
