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
 * This parameter set fulfills the [DestinationRequirements]. It implements the necessary parameters which should be
 * applied to the destination choice utility function if the next activity of an agent is considered "business", which
 * is up to the utility function to decide for a given activity type.
 */
@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
object BusinessParameters : DestinationRequirements {
    override val constant: CombinedDestinationParameters =
        object : CombinedDestinationParameters, StandardDestinationDistance, StandardDestinationParkstress {
            override val base: Double = 0.0
            override val upToOneKilometer: Double = 1.8225226612905 + 0.6
            override val betweenOneAndTwoKilometers: Double = -3.00441604544117
            override val parkingBaseline: Double = -0.0270300510475962
            override val parkingElasticity: Double = 1.15684447400216
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
            override val maxAttractiveness: Double = 1000000.0
            override val base: Double = 0.409735422083369 - 0.2

            override val isUmland: Double = 0.24569043020449 - 0.1
            override val upToOneKilometer: Double = -0.00110402064351681
            override val betweenOneAndTwoKilometers: Double = 0.37436823380068
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
            override val base: Double = 0.0754407278848996
            override val isUmland: Double = 0.181486890981761
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
            override val base: Double = -0.00609013385881508 + 0.05
            override val isUmland: Double = 0.66904907762132
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
            override val base: Double = 0.421738707446908
            override val isUmland: Double = 0.0
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
            override val base: Double = 0.207422914381344
            override val isUmland: Double = 0.0
        }
}
