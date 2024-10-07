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

/**
 * Implements the [DestinationRequirements] for shopping parameters.
 */
@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
object ShoppingParameters : DestinationRequirements {
    override val constant: CombinedDestinationParameters = object :
        CombinedDestinationParameters,
        StandardDestinationDistance,
        StandardDestinationParkstress {
        override val base: Double = 0.0
        override val upToOneKilometer: Double = 1.15908678929178
        override val betweenOneAndTwoKilometers: Double = -2.0379997781064 - 0.4
        override val parkingBaseline: Double = -0.00843930615237494
        override val parkingElasticity: Double = 1.60834206966243
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
            override val seniors: Double = 0.3
            override val venerables: Double = 0 + 0.3
            override val employmentEducation: Double = 0.0
            override val employmentWork: Double = 0.3
            override val commuterTicket: Double = 0.0
            override val surplusCars: Double = -0.1
            override val highIncome: Double = 0.0
            override val base: Double = -0.00872103866339558 + 0.1
            override val isUmland: Double = 0.380727090269338 - 0.4
            override val upToOneKilometer: Double = -0.0830385397915638
            override val betweenOneAndTwoKilometers: Double = 0.28714553219983
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
            override val venerables: Double = 0 - 0.1
            override val employmentEducation: Double = 0.0
            override val employmentWork: Double = 0.0
            override val commuterTicket: Double = 0.0
            override val surplusCars: Double = 0.0
            override val base: Double = -0.19823371290989 + 0.3
            override val isUmland: Double = 0.421365895111718 + 1.2
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
            override val venerables: Double = 0 - 0.1
            override val employmentEducation: Double = 0.0
            override val employmentWork: Double = 0.0
            override val commuterTicket: Double = 0.0
            override val surplusCars: Double = 0.0
            override val base: Double = 0.354579110282377
            override val isUmland: Double = -0.132139608629828 + 1.2
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
            override val venerables: Double = 0 - 0.1
            override val employmentEducation: Double = 0.0
            override val employmentWork: Double = 0.0
            override val commuterTicket: Double = 0.0
            override val surplusCars: Double = 0.0
            override val base: Double = 0.446866541192657
            override val isUmland: Double = 0.103132236476139 + 0.5
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
            override val venerables: Double = 0 - 0.1
            override val employmentEducation: Double = 0.0
            override val employmentWork: Double = 0.0
            override val commuterTicket: Double = 0.0
            override val surplusCars: Double = 0.0
            override val base: Double = 0.391248966496339
            override val isUmland: Double = -0.111025571955826 + 0.5
        }
}
