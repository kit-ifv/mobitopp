
import domain.shared.enums.LegacyMode
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

data class Situation(val cost: Double, val travelTime: Double, val hasCommuterTicket: Double)
typealias Mode = LegacyMode


data class Parameters(
    val asc_car: Double = 1.2,
    val asc_bus: Double = 0.2,
    val b_tt: Double = -0.1,
    val b_cost: Double = -0.5,
)

val exampleModel = DiscreteStructure<Mode, Situation, Parameters> {

    option(Mode.BIKE) { mode, sit ->
        b_tt * sit.travelTime
    }

    option(Mode.CAR) { mode, sit ->
        asc_car + b_tt * sit.travelTime + b_cost * sit.cost
    }

    option(Mode.PUBLICTRANSPORT) { mode, sit ->
        asc_car + b_tt * sit.travelTime + b_cost * sit.cost* (1 - sit.hasCommuterTicket)
        // NO PUT COST IF OWNS TICKET
    }
}.multinomialLogit(name = "ExampleModeChoiceModel").build(Parameters())
