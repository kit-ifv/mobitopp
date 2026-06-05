package edu.kit.ifv.domain.synthesis.results.legacy
import edu.kit.ifv.domain.synthesis.attributes.household.MaximumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.results.CSVOutput

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
