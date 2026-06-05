package edu.kit.ifv.domain.synthesis.results.legacy
import edu.kit.ifv.domain.synthesis.SynthesisSteps
import edu.kit.ifv.domain.synthesis.attributes.household.MaximumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MaximumPersonAttributes

fun <C, T> SynthesisSteps<*, C, T>.writeLegacyOutput()
        where C : MaximumHouseholdAttributes, T : MaximumPersonAttributes {
    LegacyHouseholdOutput<C>().writeCSVToFile(
        outputDirectory.resolve("household.csv"),
        households,
    )
    LegacyPersonOutput<C, T>().writeCSVToFile(outputDirectory.resolve("person.csv"), people)
    LegacyFixedDestinationOutput.writeCSVToFile(
        outputDirectory.resolve("fixeddestination.csv"),
        fixedDestinations,
    )
    LegacyActivityOutput.writeCSVToFile(
        outputDirectory.resolve("activity.csv"),
        activities.map { it.key to it.value },
    )
    LegacyCarOutput.writeCSVToFile(outputDirectory.resolve("car.csv"), cars)
//    LegacyOpportunitiesOutput.writeCSVToFile(
//        outputDirectory.resolve("opportunities.csv"),
//        opportunities,
//    )
}
