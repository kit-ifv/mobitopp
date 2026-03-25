package domain.synthesis.results.fastcsv

import domain.shared.datastructure.schedule.Activity
import domain.synthesis.behavior.cars.SynthesisCar
import domain.synthesis.SynthesisPerson
import domain.synthesis.results.FixedDestinationElements
import domain.synthesis.results.OpportunityOutput
import java.io.Writer


fun Collection<SynthesisCar>.writeCars(writer: Writer): Nothing = TODO()
fun Collection<Map<SynthesisPerson<*, *>, Collection<Activity>>>.writeActivities(writer: Writer): Nothing = TODO()
fun Collection<FixedDestinationElements>.writeFixedDestinations(writer: Writer): Nothing = TODO()
fun Collection<OpportunityOutput>.writeOpportunities(writer: Writer): Nothing = TODO()