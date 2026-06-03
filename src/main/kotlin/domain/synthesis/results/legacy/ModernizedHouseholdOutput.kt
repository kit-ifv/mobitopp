package domain.synthesis.results.legacy

import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.results.CSVOutput

object ModernizedHouseholdOutput : CSVOutput<SynthesisHousehold<MaximumHouseholdAttributes, *>> {
    override val header: List<String> = listOf(
        "id",
        "zoneId",
        "surveyHouseholdId",
        "location",
        "longitudeDegrees",
        "latitudeDegrees",
        "amountOfCars",
        "economicStatusCode",
    )

    override fun convert(element: SynthesisHousehold<MaximumHouseholdAttributes, *>): String = element.run {
        val location = this.attributes.location
        toCSV(
            id,
            location.zoneId,
            surveyHouseholdId,
            location,
            location.position.x,
            location.position.y,
//                "TODO nomberofnotsimulatdchildren",
            attributes.amountOfCars,
//                "TODO incomeclass",
            attributes.economicStatus.code,
        )
    }
}
