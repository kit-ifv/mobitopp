package usecases.choicemodels.modechoice.parameters

import domain.enums.Mode
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import usecases.choicemodels.modechoice.Alpha
import usecases.choicemodels.modechoice.CustomPreviousMode
import usecases.choicemodels.modechoice.ModePersonScope
import usecases.choicemodels.modechoice.NoCost
import usecases.choicemodels.modechoice.StandardActivities
import usecases.choicemodels.modechoice.StandardAge
import usecases.choicemodels.modechoice.StandardCars
import usecases.choicemodels.modechoice.StandardDrivingLicence
import usecases.choicemodels.modechoice.StandardGender
import usecases.choicemodels.modechoice.StandardHasCommuterTicket
import usecases.choicemodels.modechoice.TravelTimeActivities
import usecases.choicemodels.modechoice.TravelTimeBeta

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
internal class PassengerParameters(passenger: Mode, purposes: ChoiceModelPurposes) : NoCost {
    override val alpha: Alpha = object :
        Alpha,
        CustomPreviousMode,
        StandardAge,
        StandardHasCommuterTicket,
        StandardCars,
        StandardActivities,
        StandardDrivingLicence,
        StandardGender {
        override val mode: Mode = passenger

        override val female: Double = 0.893117885972587
        override val minors: Double = 3.11291433770843 + 0.3
        override val youngAdults: Double = 0.226300538038417 - 0.8
        override val adults: Double = 0.0
        override val commuterTicket: Double = 0.0980079892366568
        override val numberOfCars: Double = -0.094827093407709
        override val work: Double = -1.39149377320426 - 0.7
        override val education: Double = -1.5
        override val business: Double = -0.42509634025244 + 2
        override val leisure: Double = 0.0
        override val service: Double = -1.08327529817799 + 0.3
        override val shopping: Double = 0.0
        override val drivingLicence: Double = -0.663019506336669
        override val constant: Double = -5.42649021160325 - 1.5 - 0.5 + 0.3 + 0.2
        override val purposes: ChoiceModelPurposes = purposes

        override fun evaluatePreviousMode(person: ModePersonScope): Double {
            // TODO naked parameter in this function. Find a better solution for previous mode
            return if (person.previousMode == mode) 2.829225 else 0.0
        }
    }
    override val travelTimeBeta: TravelTimeBeta = object : TravelTimeBeta(), TravelTimeActivities {
        override val work: Double = 0.00658159982603899
        override val leisure: Double = 0.0136562607953001 + 0.01
        override val business: Double = -0.0168757023596564
        override val constant: Double = -0.0406043488494389 + 0.005
        override val purposes: ChoiceModelPurposes = purposes
    }
}
