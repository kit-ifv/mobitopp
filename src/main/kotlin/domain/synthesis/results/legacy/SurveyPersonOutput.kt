package domain.synthesis.results.legacy

import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.results.CSVOutput

object SurveyPersonOutput : CSVOutput<SurveyPerson<MaximumPersonAttributes>> {
    override val header: List<String> = listOf(
        "personId",
        "age",
        "gender",
        "householdIncome",
        "hasBike",
        "hasLicence",
    )

    override fun convert(element: SurveyPerson<MaximumPersonAttributes>): String = element.run {
        toCSV(
            personId,
            age,
            sex,
            "TODO Household Income is not part of person", // attributes.householdIncome.inEuros,
            attributes.hasBicycle,
            attributes.hasLicence,
        )
    }
}
