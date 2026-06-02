@file:Suppress("StringLiteralDuplication")

package application.steps.results

import application.steps.HasHouseholdRepo
import application.steps.HasImpedance
import application.steps.HasPersonAgentRepo
import application.steps.ResultsConfig
import core.modelsteps.Context
import core.results.plots.asHistogram
import core.results.plots.asLinePlot
import core.results.plots.boolColor
import core.results.plots.modeStringColor
import core.results.plots.randomColor
import domain.shared.behavior.ChoiceModelModes
import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.enums.ActivityType
import domain.shared.enums.MODEUNKOWN
import domain.shared.enums.areatype.RegioStaR7
import domain.simulation.agent.PersonAgent
import domain.synthesis.data.household.Household
import domain.synthesis.data.person.Employment
import domain.synthesis.data.person.IPerson
import domain.synthesis.data.person.sharingMembershipIds
import java.nio.file.Path
import java.time.DayOfWeek

@Suppress("LongMethod", "CyclomaticComplexMethod", "CognitiveComplexMethod", "LongParameterList")
context(config: ResultsConfig)
fun <C> C.addDefaultMIDComparisonPlots(
    subDir: String = "mid-comparison",
    midPersonPath: Path,
    midLegPath: Path,
    choiceModelPurposes: ChoiceModelPurposes,
    defaultPurpose: ActivityType,
    choiceModelModes: ChoiceModelModes,
) where C : Context, C : HasImpedance, C : HasPersonAgentRepo<*, PersonAgent>, C : HasHouseholdRepo<*, Household> {
    fun <G> midPlotForPerson(
        personFilter: (IPerson) -> Boolean = { true },
        rowFilter: (MidPersonRow) -> Boolean = { true },
        personGroup: (IPerson) -> G,
        midGroup: (MidPersonRow) -> G,
        normalize: Boolean = true,
    ) = midComparisonPlotForPerson(
        personAgentRepository,
        midPersonPath,
        personFilter,
        rowFilter,
        personGroup,
        midGroup,
        normalize,
    )

    fun <G> midPlotForLegs(
        legFilter: (PersonLeg) -> Boolean = { true },
        rowFilter: (MidLegRow) -> Boolean = { true },
        legGroup: (PersonLeg) -> G,
        midGroup: (MidLegRow) -> G,
        normalize: Boolean = true,
    ) = midComparisonPlotForLegs(
        personAgentRepository, midLegPath, choiceModelPurposes, choiceModelModes,
        impedance, legFilter, rowFilter, legGroup, midGroup, normalize,
    )

    // 1 gender age Histogram abs
    addPlot(subDir, "population", "age") {
        midPlotForPerson(
            rowFilter = { it.row("gender") != "NA" },
            personGroup = { it.sex },
            midGroup = { it.sex },
            normalize = false,
        ).overAge().asHistogram {
            name = "age distribution by sex"
            xAxisLabel = "age"
            stackAxisLabel = "sex"
            coloring = { boolColor(it.isFemale()) }
        }
    }

    // 2 occupation age Anteile rel
    addPlot(subDir, "population", "age") {
        midPlotForPerson(
            personGroup = { it.employment.simplifyEmploymentMID() },
            midGroup = { it.employment },
        ).overAge().asHistogram {
            name = "employment distribution by age"
            xAxisLabel = "age"
            stackAxisLabel = "employment"
            coloring = { randomColor() }
        }
    }

    // 3 education age Anteile rel
    addPlot(subDir, "population", "age") {
        midPlotForPerson(
            personGroup = { it.graduation.toEducationMID() },
            midGroup = { it.education },
        ).overAge().asHistogram {
            name = "education by age"
            xAxisLabel = "age"
            stackAxisLabel = "education"
            coloring = { randomColor() }
        }
    }

    // 4 driversLicense age Anteile rel
    addPlot(subDir, "population", "age") {
        midPlotForPerson(
            personGroup = { it.hasLicense },
            midGroup = { it.hasLicense },
        ).overAge().asHistogram {
            name = "drivers license distribution by age"
            xAxisLabel = "age"
            stackAxisLabel = "has drivers license"
            coloring = ::boolColor
        }
    }

    // 5 economicStatus hhSize Anteile rel
    addPlot(subDir, "population", "age") {
        midPlotForPerson(
            personGroup = { it.household.economicStatus.simplifyMID() },
            midGroup = { it.economicStatus },
        ).overHousehold(
            { householdSizeMID },
            { householdSize },
        ).asHistogram {
            name = "economic status by household size"
            xAxisLabel = "household size"
            stackAxisLabel = "economic status"
            coloring = { randomColor() }
        }
    }

    // 6 hhNumberOfCars age Anteile rel
    addPlot(subDir, "population", "age") {
        midPlotForPerson(
            personGroup = { it.carOwnershipMID() },
            midGroup = { it.hhNumberOfCars },
        ).overAge().asHistogram {
            name = "number of cars by age"
            xAxisLabel = "age"
            stackAxisLabel = "number of cars"
            coloring = { randomColor() }
        }
    }

    // 7 hhNumberOfCars gender Anteile rel
    addPlot(subDir, "population") {
        midPlotForPerson(
            rowFilter = { it.row("gender") != "NA" },
            personGroup = { it.carOwnershipMID() },
            midGroup = { it.hhNumberOfCars },
        ).over(
            { sex },
            { sex },
        ).asHistogram {
            name = "number of cars by sex"
            xAxisLabel = "sex"
            stackAxisLabel = "number of cars"
            coloring = { randomColor() }
        }
    }

    // 8 hhNumberOfCars occupation Anteile rel
    addPlot(subDir, "population", "cars") {
        midPlotForPerson(
            personGroup = { it.carOwnershipMID() },
            midGroup = { it.hhNumberOfCars },
        ).over(
            { employment.simplifyEmploymentMID() },
            { employment },
        ).asHistogram {
            name = "number of cars by occupation"
            xAxisLabel = "occupation"
            stackAxisLabel = "number of cars"
            coloring = { randomColor() }
        }
    }

    // 9 hhNumberOfCars economicStatus Anteile rel
    addPlot(subDir, "population", "cars") {
        midPlotForPerson(
            personGroup = { it.carOwnershipMID() },
            midGroup = { it.hhNumberOfCars },
        ).overHousehold(
            { economicStatus.simplifyMID() },
            { economicStatus },
        ).asHistogram {
            name = "number of cars by economic status"
            xAxisLabel = "economic status"
            stackAxisLabel = "number of cars"
            coloring = { randomColor() }
        }
    }

    // 10 hhNumberOfCars regioStar7 Anteile rel
    addPlot(subDir, "population", "cars") {
        midPlotForPerson(
            personGroup = { it.carOwnershipMID() },
            midGroup = { it.hhNumberOfCars },
        ).overHousehold(
            { location.regionType.toRegioStaR17().toRegioStaR7().code },
            { regioStaR7.code },
        ).asHistogram {
            name = "number of cars by region type"
            xAxisLabel = "region type [RegioStaR7]"
            stackAxisLabel = "number of cars"
            coloring = { randomColor() }
        }
    }

    // TODO 11 hhNumberOfCars planningArea Anteile rel

    // 12 hasTransitPass planningArea Anteile rel
    addPlot(subDir, "population", "pt") {
        midPlotForPerson(
            personGroup = { it.hasCommuterTicket },
            midGroup = { it.hasCommuterTicket },
        ).overAge().asHistogram {
            name = "commuter ticket by age"
            xAxisLabel = "age"
            stackAxisLabel = "has commuter ticket"
            coloring = ::boolColor
        }
    }

    // 13 hasTransitPass gender Anteile rel
    addPlot(subDir, "population", "pt") {
        midPlotForPerson(
            rowFilter = { it.row("gender") != "NA" },
            personGroup = { it.hasCommuterTicket },
            midGroup = { it.hasCommuterTicket },
        ).over(
            { sex },
            { sex },
        ).asHistogram {
            name = "commuter ticket by sex"
            xAxisLabel = "sex"
            stackAxisLabel = "has commuter ticket"
            coloring = ::boolColor
        }
    }

    // 14 hasTransitPass occupation Anteile rel
    addPlot(subDir, "population", "pt") {
        midPlotForPerson(
            personGroup = { it.hasCommuterTicket },
            midGroup = { it.hasCommuterTicket },
        ).over(
            { employment.simplifyEmploymentMID() },
            { employment },
        ).asHistogram {
            name = "commuter ticket by occupation"
            xAxisLabel = "occupation"
            stackAxisLabel = "has commuter ticket"
            coloring = ::boolColor
        }
    }

    // 15 hasTransitPass regioStar7 Anteile rel
    addPlot(subDir, "population", "pt") {
        midPlotForPerson(
            personGroup = { it.hasCommuterTicket },
            midGroup = { it.hasCommuterTicket },
        ).overHousehold(
            { location.regionType.toRegioStaR17().toRegioStaR7().code },
            { regioStaR7.code },
        ).asHistogram {
            name = "commuter ticket by region type"
            xAxisLabel = "region type [RegioStaR7]"
            stackAxisLabel = "has commuter ticket"
            coloring = ::boolColor
        }
    }

    // TODO 16 hasTransitPass planningArea Anteile rel

    // 17 carsharingMembership planningArea Anteile rel
    addPlot(subDir, "population", "carsharing") {
        midPlotForPerson(
            personGroup = { it.sharingMembershipIds.isNotEmpty() },
            midGroup = { it.isCarsharingMember },
        ).overAge().asHistogram {
            name = "carsharing member by age"
            xAxisLabel = "age"
            stackAxisLabel = "is carsharing member"
            coloring = ::boolColor
        }
    }

    // 18 carsharingMembership gender Anteile rel
    addPlot(subDir, "population", "carsharing") {
        midPlotForPerson(
            rowFilter = { it.row("gender") != "NA" },
            personGroup = { it.sharingMembershipIds.isNotEmpty() },
            midGroup = { it.isCarsharingMember },
        ).over(
            { sex },
            { sex },
        ).asHistogram {
            name = "carsharing member by sex"
            xAxisLabel = "sex"
            stackAxisLabel = "is carsharing member"
            coloring = ::boolColor
        }
    }

    // 19 carsharingMembership occupation Anteile rel
    addPlot(subDir, "population", "carsharing") {
        midPlotForPerson(
            personGroup = { it.sharingMembershipIds.isNotEmpty() },
            midGroup = { it.isCarsharingMember },
        ).over(
            { employment.simplifyEmploymentMID() },
            { employment },
        ).asHistogram {
            name = "carsharing member by occupation"
            xAxisLabel = "occupation"
            stackAxisLabel = "is carsharing member"
            coloring = ::boolColor
        }
    }

    // 20 carsharingMembership regioStar7 Anteile rel
    addPlot(subDir, "population", "carsharing") {
        midPlotForPerson(
            personGroup = { it.sharingMembershipIds.isNotEmpty() },
            midGroup = { it.isCarsharingMember },
        ).overHousehold(
            { location.regionType.toRegioStaR17().toRegioStaR7().code },
            { regioStaR7.code },
        ).asHistogram {
            name = "carsharing member by region type"
            xAxisLabel = "region type [RegioStaR7]"
            stackAxisLabel = "is carsharing member"
            coloring = ::boolColor
        }
    }

    // TODO 21 carsharingMembership planningArea Anteile rel

    // 1 Purpose	DistanceCategories	Anteile	rel
    addPlot(subDir, "trips", "distance") {
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

    // 2 Purpose	DistanceCategories	Histogramm	abs
    addPlot(subDir, "trips", "distance") {
        midPlotForLegs(
            legGroup = { it.purpose?.simplifyMID(choiceModelPurposes) ?: defaultPurpose }, // TODO check undefined
            midGroup = { it.activityType },
            normalize = false,
        ).overDistance().asHistogram {
            name = "relative travel distance distribution by purpose"
            stackAxisLabel = "purpose"
            xAxisLabel = "travel distance [km]"
            coloring = { randomColor() }
        }
    }

    // 3 Mode	DistanceCategories	Anteile	rel
    addPlot(subDir, "trips", "distance") {
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

    // 4 Mode	DistanceCategories	Histogramm	abs
    addPlot(subDir, "trips", "distance") {
        midPlotForLegs(
            legFilter = { it.leg.transportType != MODEUNKOWN },
            legGroup = { it.leg.transportType },
            midGroup = { it.mode },
            normalize = false,
        ).overDistance().asHistogram {
            name = "relative travel distance distribution by mode"
            stackAxisLabel = "mode"
            xAxisLabel = "travel distance [km]"
            coloring = { modeStringColor(it.description) }
        }
    }

    // 5 Mode	DistanceCategories	Anteile	rel	Employment
    for (employment in Employment.entries.map { it.simplifyEmploymentMID() }.distinct()) {
        addPlot(subDir, "trips", "distance", "by_employment") {
            midPlotForLegs(
                legFilter = {
                    it.leg.transportType != MODEUNKOWN && it.person.employment.simplifyEmploymentMID() == employment
                },
                rowFilter = { it.employment == employment },
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

    // 6 Mode	DistanceCategories	Anteile	rel	carOwnership
    for (hasCar in listOf(false, true)) {
        val label = if (hasCar) "household with car" else "household without car"
        addPlot(subDir, "trips", "distance", "by_car_ownership") {
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

    // 7 Mode	DistanceCategories	Anteile	rel	hasTransitPass
    for (hasTicket in listOf(false, true)) {
        val label = if (hasTicket) "person with commuter ticket" else "person without commuter ticket"
        addPlot(subDir, "trips", "distance", "by_ticket_ownership") {
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

    // 8 Mode	DistanceCategories	Anteile	rel	purpose
    for (purpose in choiceModelPurposes.allActivityTypes.map { it.simplifyMID(choiceModelPurposes) }.distinct()) {
        addPlot(subDir, "trips", "distance", "by_purpose") {
            midPlotForLegs(
                legFilter = {
                    it.leg.transportType != MODEUNKOWN &&
                        (it.purpose?.simplifyMID(choiceModelPurposes) ?: defaultPurpose) == purpose
                },
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

    // 9 Mode	DistanceCategories	Anteile	rel	regioStaR7
    for (regio in RegioStaR7.entries) {
        addPlot(subDir, "trips", "distance", "by_regiostar") {
            midPlotForLegs(
                legFilter = {
                    it.leg.transportType != MODEUNKOWN &&
                        it.person.household.location.regionType.toRegioStaR17()?.toRegioStaR7() == regio
                },
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

    // 10 Purpose	TravelTimeCategories	Anteile	rel
    addPlot(subDir, "trips", "time") {
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

    // 11 Mode	TravelTimeCategories	Anteile	rel
    addPlot(subDir, "trips", "time") {
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

    // 12 Mode	beginTrip	Timeseries	rel
    addPlot(subDir, "trips", "time") {
        midPlotForLegs(
            legFilter = { it.leg.transportType != MODEUNKOWN && it.leg.startTime.weekDay == DayOfWeek.MONDAY },
            rowFilter = { it.tripStart != null },
            legGroup = { it.leg.transportType },
            midGroup = { it.mode },
            normalize = false,
        ).overTripStart().asLinePlot {
            name = "time series (trip starts on monday) by mode"
            xAxisLabel = "time"
            groupAxisLabel = "mode"
            yAxisLabel = "rel. count"
            coloring = { modeStringColor(it.description) }
        }
    }

    // 1 Purpose	beginActivity	Histogramm	rel
    addPlot(subDir, "activity") {
        midPlotForLegs(
            legGroup = { it.purpose?.simplifyMID(choiceModelPurposes) ?: defaultPurpose },
            rowFilter = { it.activityStart != null },
            midGroup = { it.activityType },
            normalize = false,
        ).overActivityStart().asLinePlot {
            name = "time series (activity starts on monday) by purpose"
            xAxisLabel = "time"
            groupAxisLabel = "purpose"
            yAxisLabel = "rel. count"
            coloring = { randomColor() }
        }
    }

    // 2 Purpose	age	Anteile	abs
    addPlot(subDir, "activity") {
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

    // 3 Purpose	occupation	Anteile	abs
    addPlot(subDir, "activity") {
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
