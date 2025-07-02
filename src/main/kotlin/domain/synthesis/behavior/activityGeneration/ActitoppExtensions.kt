package synthesis.activityGeneration

import synthesis.SurveyInfo
import synthesis.domain.SynthesisHousehold

@Suppress("MagicNumber") // TODO this may be relevant to fix, age 10 is magic
val SynthesisHousehold<out SurveyInfo>.numberOfChilds get() = members.count { it.age <= 10 }

@Suppress("MagicNumber") // TODO this may be relevant to fix, age 10 is magic
val SynthesisHousehold<out SurveyInfo>.numberOfYouths get() = members.count { it.age in 10..<18 }
