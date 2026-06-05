package edu.kit.ifv.domain.synthesis.results.legacy
import edu.kit.ifv.domain.synthesis.SynthesisPerson
import edu.kit.ifv.domain.synthesis.attributes.household.MaximumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MaximumPersonAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.employment
import edu.kit.ifv.domain.synthesis.results.CSVOutput

@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
class LegacyPersonOutput<C : MaximumHouseholdAttributes, T : MaximumPersonAttributes> :
    CSVOutput<SynthesisPerson<C, T>> {
    private val surveyDummy = "BIKE=0.0,CAR=0.0,PASSENGER=0.0,PEDESTRIAN=0.0,PUBLICTRANSPORT=0.0"
    override val header: List<String> = SurveyPersonOutput.header + listOf(

        "personNumber",
        "householdId",
        "employment",
        "hasAccessToCar",
        "hasPersonalCar",
        "hasCommuterTicket",
        "hasLicense",
        "preferencesSurvey",
        "preferencesSimulation",
        "eMobilityAcceptance",
        "chargingInfluencesDestinationChoice",
        "mobilityProviderCustomership",

    )

    @Suppress("MagicNumber")
    override fun convert(element: SynthesisPerson<C, T>): String {
        val first = SurveyPersonOutput.convert(element)
        val second = element.run {
            toCSV(
                -1, // Dummy value. person Number is not a useful attribute
                householdId,

                employment,
                household.hasCars(),
                "TODO is this field sth useful?", // household.amountOfCars <= household.numberOfDrivingLicences,
                attributes.hasTransitPass,
                attributes.hasLicence,
                surveyDummy,
                surveyDummy,
                0.5,
                "NEVER",
                this.getSharingMemberships(),

            )
        }
        return "$first;$second"
    }
}
