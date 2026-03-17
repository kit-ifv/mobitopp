package domain.synthesis.behavior.activityGeneration

import domain.synthesis.behavior.ISurveyHousehold

@Suppress("MagicNumber") // TODO this may be relevant to fix, age 10 is magic
val ISurveyHousehold<*, *>.numberOfChilds get() = members.count { it.age <= 10 }

@Suppress("MagicNumber") // TODO this may be relevant to fix, age 10 is magic
val ISurveyHousehold<*,*>.numberOfYouths get() = members.count { it.age in 10..<18 }
