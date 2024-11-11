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
 * Implements the [DestinationRequirements] for Service activity types.
 */
@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
internal object ServiceParameters : DestinationRequirements {
    override val constant: CombinedDestinationParameters = object :
        CombinedDestinationParameters,
        StandardDestinationDistance,
        StandardDestinationParkstress {
        override val base: Double = .0
        override val upToOneKilometer: Double = 2.28256790230704 - 1.4 - 0.2
        override val betweenOneAndTwoKilometers: Double = -0.671395833753517 - 0.9
        override val parkingBaseline: Double = -0.0120845537785062
        override val parkingElasticity: Double = 1.6395951609136
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
            override val maxAttractiveness: Double = 8000.0
            override val youngAdults: Double = 0.0
            override val adults: Double = 0.0
            override val seniorAdults: Double = 0.0
            override val seniors: Double = 0.0
            override val venerables: Double = 0.0
            override val employmentEducation: Double = 0.0
            override val employmentWork: Double = 0.0
            override val commuterTicket: Double = 0.0
            override val surplusCars: Double = 0.0
            override val highIncome: Double = 0.0
            override val base: Double = 0.0368075625861074 + 0.05
            override val isUmland: Double = 0.33971123395175 - 0.2
            override val upToOneKilometer: Double = -0.196340350043379
            override val betweenOneAndTwoKilometers: Double = 0.168692001737065
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
            override val base: Double = 0.112014977031613 + 0.1
            override val isUmland: Double = -0.107952220575169 + 0.5
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
            override val base: Double = 0.305120325951206
            override val isUmland: Double = -0.0583596066954709 + 0.5
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
            override val base: Double = 0.47897421843231
            override val isUmland: Double = 0.111554794703124 + 0.1
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
            override val base: Double = 0.365937445996849
            override val isUmland: Double = -0.0589858168127732 + 0.1
        }
}
