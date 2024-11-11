package usecases.choicemodels.destinationchoice.parameters

import usecases.choicemodels.destinationchoice.AttractivenessParameters
import usecases.choicemodels.destinationchoice.CombinedDestinationParameters
import usecases.choicemodels.destinationchoice.DestCommuterTicket
import usecases.choicemodels.destinationchoice.DestinationAge
import usecases.choicemodels.destinationchoice.DestinationEconomicStatus
import usecases.choicemodels.destinationchoice.DestinationEmployment
import usecases.choicemodels.destinationchoice.DestinationRequirements
import usecases.choicemodels.destinationchoice.DestinationUmland
import usecases.choicemodels.destinationchoice.StandardDestinationDistance
import usecases.choicemodels.destinationchoice.StandardDestinationParkstress
import usecases.choicemodels.destinationchoice.SurplusCars

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
internal object LeisureParameters : DestinationRequirements {
    override val constant: CombinedDestinationParameters = object :
        CombinedDestinationParameters,
        StandardDestinationDistance,
        StandardDestinationParkstress {
        override val base: Double = 0.0
        override val upToOneKilometer: Double = 4.01726106476279
        override val betweenOneAndTwoKilometers: Double = -1.2807770160566 + 0.4
        override val parkingBaseline: Double = -0.000805225283993312
        override val parkingElasticity: Double = 2.03231417905929
    }
    override val attractiveness: AttractivenessParameters =
        object :
            AttractivenessParameters,
            DestinationAge,
            DestinationEmployment,
            DestCommuterTicket,
            SurplusCars,
            DestinationEconomicStatus,
            DestinationUmland,
            StandardDestinationDistance {
            override val maxAttractiveness: Double = 20000.0
            override val youngAdults: Double = 0.0
            override val adults: Double = 0.0
            override val seniorAdults: Double = 0.0
            override val seniors: Double = 0.0
            override val venerables: Double = 0 + 0.1
            override val employmentEducation: Double = 0 - 0.05
            override val employmentWork: Double = 0 + 0.1
            override val commuterTicket: Double = 0.0
            override val surplusCars: Double = 0.1
            override val highIncome: Double = 0.0
            override val base: Double = 0.164790334596064 - 0.1
            override val isUmland: Double = 0.610291409419243 - 0.2
            override val upToOneKilometer: Double = -0.391440604191497
            override val betweenOneAndTwoKilometers: Double = 0.165687668821311
        }

    override val carLogsum: CombinedDestinationParameters =
        object :
            CombinedDestinationParameters,
            DestinationAge,
            DestinationEmployment,
            DestCommuterTicket,
            SurplusCars,
            DestinationEconomicStatus,
            DestinationUmland {
            override val highIncome: Double = 0.0
            override val youngAdults: Double = 0.0
            override val adults: Double = 0.0
            override val seniorAdults: Double = 0.0
            override val seniors: Double = 0.0
            override val venerables: Double = 0.0
            override val employmentEducation: Double = 0.0
            override val employmentWork: Double = 0.0
            override val commuterTicket: Double = 0.0
            override val surplusCars: Double = 0.0
            override val base: Double = 0.101571780926651 - 0.05
            override val isUmland: Double = 0.0462160663655077 + 0.1
        }
    override val carLogsumFix: CombinedDestinationParameters =
        object :
            CombinedDestinationParameters,
            DestinationAge,
            DestinationEmployment,
            DestCommuterTicket,
            SurplusCars,
            DestinationEconomicStatus,
            DestinationUmland {
            override val highIncome: Double = 0.0
            override val youngAdults: Double = 0.0
            override val adults: Double = 0.0
            override val seniorAdults: Double = 0.0
            override val seniors: Double = 0.0
            override val venerables: Double = 0.0
            override val employmentEducation: Double = 0.0
            override val employmentWork: Double = 0.0
            override val commuterTicket: Double = 0.0
            override val surplusCars: Double = 0.0
            override val base: Double = 0.321408622664327
            override val isUmland: Double = -0.0755006569431623 + 0.1
        }
    override val flexLogsum: CombinedDestinationParameters =
        object :
            CombinedDestinationParameters,
            DestinationAge,
            DestinationEmployment,
            DestCommuterTicket,
            SurplusCars,
            DestinationEconomicStatus,
            DestinationUmland {
            override val highIncome: Double = 0.0
            override val youngAdults: Double = 0.0
            override val adults: Double = 0.0
            override val seniorAdults: Double = 0.0
            override val seniors: Double = 0.0
            override val venerables: Double = 0.0
            override val employmentEducation: Double = 0.0
            override val employmentWork: Double = 0.0
            override val commuterTicket: Double = 0.0
            override val surplusCars: Double = 0.0
            override val base: Double = 0.37913097643898 - 0.1
            override val isUmland: Double = 0.0640383675977011 + 0.1
        }
    override val flexFixLogsum: CombinedDestinationParameters =
        object :
            CombinedDestinationParameters,
            DestinationAge,
            DestinationEmployment,
            DestCommuterTicket,
            SurplusCars,
            DestinationEconomicStatus,
            DestinationUmland {
            override val highIncome: Double = 0.0
            override val youngAdults: Double = 0.0
            override val adults: Double = 0.0
            override val seniorAdults: Double = 0.0
            override val seniors: Double = 0.0
            override val venerables: Double = 0.0
            override val employmentEducation: Double = 0.0
            override val employmentWork: Double = 0.0
            override val commuterTicket: Double = 0.0
            override val surplusCars: Double = 0.0
            override val base: Double = 0.0223919993684166
            override val isUmland: Double = 0.0416507340946399 + 0.1
        }
}
