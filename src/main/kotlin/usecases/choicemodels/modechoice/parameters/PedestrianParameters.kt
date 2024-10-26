package usecases.choicemodels.modechoice.parameters

import usecases.choicemodels.modechoice.Alpha
import usecases.choicemodels.modechoice.NoCost
import usecases.choicemodels.modechoice.StandardActivities
import usecases.choicemodels.modechoice.StandardAge
import usecases.choicemodels.modechoice.StandardCars
import usecases.choicemodels.modechoice.StandardDistance
import usecases.choicemodels.modechoice.TravelTimeActivities
import usecases.choicemodels.modechoice.TravelTimeBeta

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
internal object PedestrianParameters : NoCost {
    override val alpha: Alpha = object : Alpha, StandardCars, StandardAge, StandardActivities, StandardDistance {
        override val work: Double = -0.503090102175567 + 1.2
        override val education: Double = 0.3
        override val business: Double = -0.784166770512213 - 1.1
        override val leisure: Double = 1.16599124418 - 1.2
        override val service: Double = -1.76177714595929 - 0.8
        override val shopping: Double = -1.05505852162273 + 0.25
        override val numberOfCars: Double = -0.596362099668231
        override val constant: Double = 0 - 0.1 - 0.25
        override val minors: Double = 2.34054180179764 - 0.7
        override val youngAdults: Double = -0.285913206494658 - 0.3
        override val adults: Double = -0.326256906559177 + 0.1
        override val closeDistance: Double = -1.0
    }

    override val travelTimeBeta: TravelTimeBeta = object : TravelTimeBeta(), TravelTimeActivities {
        override val constant: Double = -0.148247995923713
        override val work: Double = -0.045632595478184 * 0
        override val leisure: Double = 0.0273293409702819 + 0.04
        override val business: Double = -0.0324546622169199
    }
}
