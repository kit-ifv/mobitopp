package application.steps.results

import core.modelsteps.Context
import core.results.plots.asHistogram
import core.results.plots.asLinePlot
import core.results.plots.modeStringColor
import core.results.plots.randomColor
import domain.shared.behavior.ChoiceModelModes
import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.enums.ActivityType
import domain.shared.enums.MODEUNKOWN
import domain.shared.enums.areatype.RegioStaR7
import domain.simulation.results.AgentResultsContext
import domain.simulation.results.PersonLeg
import domain.synthesis.data.Employment
import java.nio.file.Path
import java.time.DayOfWeek

fun <C> C.addDefaultMIDComparisonPlots(
    subDir: String = "mid-comparison",
    midDataPath: Path,
    choiceModelPurposes: ChoiceModelPurposes,
    defaultPurpose: ActivityType,
    choiceModelModes: ChoiceModelModes,
) where C: Context, C: AgentResultsContext = run {

    fun <G> AgentResultsContext.midPlotForLegs(
        legFilter: (PersonLeg) -> Boolean = { true },
        rowFilter: (MidLegRow) -> Boolean = { true },
        legGroup: (PersonLeg) -> G,
        midGroup: (MidLegRow) -> G,
        normalize: Boolean = true
    ) = this.midComparisonPlotForLegs(
        midDataPath, choiceModelPurposes, choiceModelModes, this.impedance.value,
        legFilter, rowFilter, legGroup, midGroup, normalize
    )

    //1 Purpose	DistanceCategories	Anteile	rel
    addPlot(subDir) {

        midPlotForLegs(
            legGroup = { it.purpose?.simplifyMID(choiceModelPurposes) ?: defaultPurpose },
            midGroup = { it.activityType },
        ).overDistance().asHistogram {
            name = "travel distance by purpose normalized"
            xAxisLabel = "travel distance [km]"
            stackAxisLabel = "trip purpose"
            coloring = { randomColor() }
        }

    }

    //2 Purpose	DistanceCategories	Histogramm	abs
    addPlot(subDir) {

        midPlotForLegs(
            legGroup = { it.purpose?.simplifyMID(choiceModelPurposes) ?: defaultPurpose }, //TODO check undefined
            midGroup = { it.activityType },
            normalize = false
        ).overDistance().asHistogram {
            name = "relative travel distance distribution by purpose"
            stackAxisLabel = "purpose"
            xAxisLabel = "travel distance [km]"
            coloring = { randomColor() }
        }

    }

//
//        addPlot {
//
//            midPlotForLegs(
//                legGroup = { },
//                midGroup = { },
//                normalize = false
//            ).overDistance().asHistogram {
//                name = "relative travel distance distribution"
//                xAxisLabel = "travel distance [km]"
//            }
//
//        }

    //3 Mode	DistanceCategories	Anteile	rel
    addPlot(subDir) {

        midPlotForLegs(
            legFilter = { it.leg.transportType != MODEUNKOWN },
            legGroup = { it.leg.transportType },
            midGroup = { it.mode },
        ).overDistance().asHistogram {
            name = "travel distance by mode normalized"
            xAxisLabel = "travel distance [km]"
            stackAxisLabel = "mode"
            coloring = { modeStringColor(it.description) }
        }

    }

    //4 Mode	DistanceCategories	Histogramm	abs
    addPlot(subDir) {

        midPlotForLegs(
            legFilter = { it.leg.transportType != MODEUNKOWN },
            legGroup = { it.leg.transportType },
            midGroup = { it.mode },
            normalize = false
        ).overDistance().asHistogram {
            name = "relative travel distance distribution by mode"
            stackAxisLabel = "mode"
            xAxisLabel = "travel distance [km]"
            coloring = { modeStringColor(it.description) }
        }

    }

    //5 Mode	DistanceCategories	Anteile	rel	Employment
    for (employment in Employment.entries.map { it.simplifyEmploymentMID() }.distinct()) {
        addPlot("${subDir}/by_employment") {
            midPlotForLegs(
                legFilter = { it.leg.transportType != MODEUNKOWN && it.person.employment.simplifyEmploymentMID() == employment },
                rowFilter = { it.employment == employment},
                legGroup = { it.leg.transportType },
                midGroup = { it.mode },
            ).overDistance().asHistogram {
                name = "travel distance by mode normalized for employment ${employment.name.lowercase()}"
                xAxisLabel = "travel distance [km]"
                stackAxisLabel = "mode"
                coloring = { modeStringColor(it.description) }
            }
        }
    }

    //6 Mode	DistanceCategories	Anteile	rel	carOwnership
    for (hasCar in listOf(false, true)) {
        val label = if(hasCar)  "household with car" else "household without car"
        addPlot("${subDir}/by_car_ownership") {
            midPlotForLegs(
                legFilter = { it.leg.transportType != MODEUNKOWN && (it.person.household.cars.isNotEmpty()) == hasCar },
                rowFilter = { (it.hhNumberOfCars != "0") == hasCar },
                legGroup = { it.leg.transportType },
                midGroup = { it.mode },
            ).overDistance().asHistogram {
                name = "travel distance by mode normalized for $label"
                xAxisLabel = "travel distance [km]"
                stackAxisLabel = "mode"
                coloring = { modeStringColor(it.description) }
            }
        }
    }

    //7 Mode	DistanceCategories	Anteile	rel	hasTransitPass
    for (hasTicket in listOf(false, true)) {
        val label = if(hasTicket)  "person with commuter ticket" else "person without commuter ticket"
        addPlot("${subDir}/by_ticket_ownership") {
            midPlotForLegs(
                legFilter = { it.leg.transportType != MODEUNKOWN && (it.person.hasCommuterTicket == hasTicket) },
                rowFilter = { it.hasCommuterTicket == hasTicket },
                legGroup = { it.leg.transportType },
                midGroup = { it.mode },
            ).overDistance().asHistogram {
                name = "travel distance by mode normalized for $label"
                xAxisLabel = "travel distance [km]"
                stackAxisLabel = "mode"
                coloring = { modeStringColor(it.description) }
            }
        }
    }

    //8 Mode	DistanceCategories	Anteile	rel	purpose
    for (purpose in choiceModelPurposes.allActivityTypes.map { it.simplifyMID(choiceModelPurposes) }.distinct()) {
        addPlot("${subDir}/by_purpose") {
            midPlotForLegs(
                legFilter = { it.leg.transportType != MODEUNKOWN && (it.purpose?.simplifyMID(choiceModelPurposes) ?: defaultPurpose) == purpose },
                rowFilter = { it.activityType == purpose },
                legGroup = { it.leg.transportType },
                midGroup = { it.mode },
            ).overDistance().asHistogram {
                name = "travel distance by mode normalized for purpose ${purpose.description.lowercase()}"
                xAxisLabel = "travel distance [km]"
                stackAxisLabel = "mode"
                coloring = { modeStringColor(it.description) }
            }
        }
    }

    //9 Mode	DistanceCategories	Anteile	rel	regioStaR7
    for (regio in RegioStaR7.entries) {
        addPlot("${subDir}/by_regiostar") {
            midPlotForLegs(
                legFilter = { it.leg.transportType != MODEUNKOWN && it.person.household.location.zone?.regionType?.toRegioStaR17()?.toRegioStaR7() == regio },
                rowFilter = { it.regioStaR7 == regio },
                legGroup = { it.leg.transportType },
                midGroup = { it.mode },
            ).overDistance().asHistogram {
                name = "travel distance by mode normalized for RegioStaR7 ${regio.code}"
                xAxisLabel = "travel distance [km]"
                stackAxisLabel = "mode"
                coloring = { modeStringColor(it.description) }
            }
        }
    }

    //10 Purpose	TravelTimeCategories	Anteile	rel
    addPlot(subDir) {

        midPlotForLegs(
            legGroup = { it.purpose?.simplifyMID(choiceModelPurposes) ?: defaultPurpose },
            midGroup = { it.activityType },
        ).overDuration().asHistogram {
            name = "travel time by purpose normalized"
            xAxisLabel = "duration [min]"
            stackAxisLabel = "trip purpose"
            coloring = { randomColor() }
        }

    }

    //11 Mode	TravelTimeCategories	Anteile	rel
    addPlot(subDir) {

        midPlotForLegs(
            legFilter = { it.leg.transportType != MODEUNKOWN },
            legGroup = { it.leg.transportType },
            midGroup = { it.mode },
        ).overDuration().asHistogram {
            name = "travel time by mode normalized"
            xAxisLabel = "duration [min]"
            stackAxisLabel = "mode"
            coloring = { modeStringColor(it.description) }
        }

    }

    //12 Mode	beginTrip	Timeseries	rel
    addPlot(subDir) {

        midPlotForLegs(
            legFilter = { it.leg.transportType != MODEUNKOWN && it.leg.startTime.weekDay == DayOfWeek.MONDAY },
            rowFilter = { it.tripStart != null },
            legGroup = { it.leg.transportType },
            midGroup = { it.mode },
            normalize = false
        ).overTripStart().asLinePlot {
            name = "time series (trip starts on monday) by mode"
            xAxisLabel = "time"
            groupAxisLabel = "mode"
            yAxisLabel = "rel. count"
            coloring = { modeStringColor(it.description) }
        }

    }

    //1 Purpose	beginActivity	Histogramm	rel
    addPlot(subDir) {

        midPlotForLegs(
            legGroup = { it.purpose?.simplifyMID(choiceModelPurposes) ?: defaultPurpose },
            rowFilter = { it.activityStart != null },
            midGroup = { it.activityType },
            normalize = false
        ).overActivityStart().asLinePlot {
            name = "time series (activity starts on monday) by purpose"
            xAxisLabel = "time"
            groupAxisLabel = "purpose"
            yAxisLabel = "rel. count"
            coloring = { randomColor() }
        }

    }

    //2 Purpose	age	Anteile	abs
    addPlot(subDir) {

        midPlotForLegs(
            legGroup = { it.purpose?.simplifyMID(choiceModelPurposes) ?: defaultPurpose },
            midGroup = { it.activityType },
        ).overAge().asHistogram {
            name = "purpose by age"
            xAxisLabel = "age"
            stackAxisLabel = "purpose"
            coloring = { randomColor() }
        }

    }

    //3 Purpose	occupation	Anteile	abs
    addPlot(subDir) {

        midPlotForLegs(
            legGroup = { it.purpose?.simplifyMID(choiceModelPurposes) ?: defaultPurpose },
            midGroup = { it.activityType },
        ).overEmployment().asHistogram {
            name = "activity type by employment"
            xAxisLabel = "employment"
            stackAxisLabel = "activity type"
            coloring = { randomColor() }
        }

    }




}