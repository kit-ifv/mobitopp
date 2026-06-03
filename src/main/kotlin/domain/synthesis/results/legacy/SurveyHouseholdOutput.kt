package domain.synthesis.results.legacy

import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.results.CSVOutput

object SurveyHouseholdOutput : CSVOutput<ISurveyHousehold<MaximumHouseholdAttributes, *>> {
    override val header: List<String> = listOf("nominalSize", "numberOfMinors", "income")

    @Suppress("MagicNumber")
    override fun convert(element: ISurveyHousehold<MaximumHouseholdAttributes, *>): String = element.run {
        toCSV(
            members.size,
            members.count { it.age < 18 },
            income.inEuros,
        )
    }
}
