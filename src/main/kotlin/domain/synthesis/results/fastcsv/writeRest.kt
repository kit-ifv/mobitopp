package domain.synthesis.results.fastcsv

import domain.shared.datastructure.schedule.Activity
import domain.synthesis.SynthesisPerson
import domain.synthesis.behavior.cars.SynthesisCar
import domain.synthesis.results.FixedDestinationElements
import domain.synthesis.results.OpportunityOutput
import java.io.Writer

fun Collection<SynthesisCar>.writeCars(writer: Writer): Nothing =
    error(writer)

fun Collection<Map<SynthesisPerson<*, *>, Collection<Activity>>>.writeActivities(
    writer: Writer,
): Nothing = error(writer)

fun Collection<FixedDestinationElements>.writeFixedDestinations(writer: Writer): Nothing =
    error(writer)

fun Collection<OpportunityOutput>.writeOpportunities(writer: Writer): Nothing =
    error(writer)
