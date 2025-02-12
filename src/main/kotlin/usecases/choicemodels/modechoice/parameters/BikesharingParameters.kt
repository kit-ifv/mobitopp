package usecases.choicemodels.modechoice.parameters

import domain.data.EconomicStatus
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import usecases.choicemodels.modechoice.Alpha
import usecases.choicemodels.modechoice.CustomAge
import usecases.choicemodels.modechoice.CustomEconomicStatus
import usecases.choicemodels.modechoice.CustomNextActivity
import usecases.choicemodels.modechoice.ModePersonScope
import usecases.choicemodels.modechoice.StandardActivities
import usecases.choicemodels.modechoice.StandardCars
import usecases.choicemodels.modechoice.StandardGender
import usecases.choicemodels.modechoice.StandardHasCommuterTicket
import usecases.choicemodels.modechoice.TravelCostBeta
import usecases.choicemodels.modechoice.TravelTimeBeta
import usecases.choicemodels.modechoice.WithCost

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
internal class BikesharingParameters(purposes: ChoiceModelPurposes) : WithCost {
    override val alpha: Alpha =
        object : Alpha, StandardHasCommuterTicket, StandardCars, StandardActivities, StandardGender, CustomAge {

            override val female: Double = -0.742975367550372
            override val commuterTicket: Double = 0.265046300898315
            override val numberOfCars: Double = -0.519466510039712
            override val work: Double = 0.755684897761409
            override val education: Double = 0.0
            override val business: Double = 0.50393908569628 - 0.2
            override val leisure: Double = 1.01847035480728
            override val service: Double = -0.441718268397706 - 1
            override val shopping: Double = 0.0
            override val constant: Double = -7.87371999724625 + 1.1 + 1.15
            override val purposes: ChoiceModelPurposes = purposes

            val youngAdults: Double = 0.880034329871052
            val adults: Double = 0.378296037637105
            val seniorAdults = -0.203289071442423

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
        override val constant: Double = -0.111596101439312
        override val purposes: ChoiceModelPurposes = purposes
    }

    override val travelCostBeta: TravelCostBeta = object : TravelCostBeta(), CustomEconomicStatus, CustomNextActivity {
        override val constant: Double =
            -0.173579310377872 // TODO make reference to main cost inheritor to link the parameter
        override val purposes: ChoiceModelPurposes = purposes
        val richGuyFactor = 0.029633074216897 // TODO this parameter is shared between multiple utility functions
        val businessFactor = 0.0840900269251931 - 0.02 // TODO as is this one
        override fun evaluateEconomicStatus(person: ModePersonScope): Double {
            return if (person.person.household.economicStatus == EconomicStatus.VERY_HIGH) richGuyFactor else 0.0
        }

        override fun evaluateNextActivity(person: ModePersonScope): Double {
            return if (person.nextActivity.type == purposes.business) businessFactor else 0.0
        }
    }
}
