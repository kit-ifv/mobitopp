package domain.synthesis.results.legacy

import domain.shared.location.toRecord
import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.results.CSVOutput

// Sorry detekt, householdId and other strings may occur more often.
@Suppress("StringLiteralDuplication", "MagicNumber")
class LegacyHouseholdOutput<T : MaximumHouseholdAttributes> : CSVOutput<SynthesisHousehold<T, *>> {

    override val header: List<String> = listOf(
        "householdId",
        "year",
        "householdNumber",
        "domCode",
        "type",
        "homeZone", // Why in the everloving F is homezone using the internal enumeration of zones: 6113 -> 0, 6114 ->1
        "actualHomeZone",
        "homeLocation",
        "homeX",
        "homeY",
        "numberOfNotSimulatedChildren",
        "totalNumberOfCars",
        "incomeClass",
        "economicalStatus",
        "canChargePrivately",
    ) + SurveyHouseholdOutput.header

    override fun convert(element: SynthesisHousehold<T, *>): String = element.run {
        val location = attributes.location
        toCSV(
            id,
            1970, // Dummy value: Originally the year from Survey Info. Now useless.
            "dummyval", // Dummy value: Originally the ID in the Survey Info.
            -1, // Ok, here I am lost, I have absolutely no idea what "domcode" is supposed to be.
            attributes.type.code, // The household type. Again taken from survey data.
            "uselessattribute", // location.zone?.legacyId ?: "NULL", // I HATE OLD MOBITOPP
            location.zoneId,
            location.toRecord().legacyStringRepresentation(),
            location.position.x,
            location.position.y,
            -1, // ActiTopp once cared about the number of children, but it is entirely irrelevant
            attributes.amountOfCars,
            5, // I would assume that this is the encoding of the income based on some classes, but used it is not.
            attributes.economicStatus.code,
            "true", // Everyone can charge privately. Why this field was added to the general output / No one knows

        )
    } + ";" + SurveyHouseholdOutput.convert(element)
}
