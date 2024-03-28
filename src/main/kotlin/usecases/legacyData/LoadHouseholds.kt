package usecases.legacyData

import CodePlan
import domain.data.EconomicStatus
import domain.data.HouseholdDataBuilder
import domain.location.RoadPosition
import domain.location.parseRoadPosition
import modeling.steps.BuildStep
import modeling.steps.Context
import modeling.steps.CsvResource
import modeling.steps.HouseholdContext
import modeling.steps.LegacyZonesContext
import modeling.steps.ModelExecution
import modeling.steps.PrepareCsvStep
import units.CurrencyUnit
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.SEMICOLON
import utils.csv.currency
import utils.csv.decode
import utils.csv.int
import utils.csv.long
import java.io.File

@Suppress("LongParameterList")
fun <S, C> S.prepareHouseholds(
    file: File? = null,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    hhNumberColumn: String = "householdId",
    yearColumn: String = "year",
    zoneColumn: String = "homeZone",
    locationColumn: String = "homeLocation",
    roadPositionParser: (String) -> RoadPosition = String::parseRoadPosition,
    domCodeColumn: String = "domCode",
    typeColumn: String = "type",
    incomeColumn: String = "income", //TODO unit: currency over time
    incomeUnit: CurrencyUnit? = null,
    economicalStatusColumn: String = "economicalStatus",
    economicalStatusCodes: CodePlan<EconomicStatus>? = null
) where S: ModelExecution<C>, C: Context, C: LegacyZonesContext, C: HouseholdContext {

    val currencyUnit = incomeUnit ?: this.context.currencyUnit
    val zoneIndex = { context.zoneColumnIndex }
    val economicalStatusCodePlan = economicalStatusCodes ?: context.economicalStatusCodes

    val parser = CsvParser(errorHandling) { row ->
        HouseholdDataBuilder(
            householdNumber = row.long(hhNumberColumn),
            surveyYear = row.int(yearColumn),
            homeZone = requireNotNull(
                zoneIndex()[row.int(zoneColumn)]
            ), // legacy household.csv files reference column instead of visum id
            roadPosition = row(locationColumn, roadPositionParser),
            domCode = row.int(domCodeColumn),
            type = row.int(typeColumn),
            incomePerMonth = row.currency(incomeColumn, currencyUnit),
            economicStatus = row.decode(economicalStatusColumn, economicalStatusCodePlan)
        )
    }

    this.prepareHouseholdsFile(parser, file, delimiter)
}

fun <S, C> S.prepareHouseholdsFile(
    parser: CsvParser<HouseholdDataBuilder>,
    file: File? = null,
    delimiter: String = SEMICOLON,
) where S: ModelExecution<C>, C: Context, C: LegacyZonesContext, C: HouseholdContext {
    val householdFile = file ?: File(this.context.demandFolder.path + "\\demand-data\\household.csv")

    val resource = CsvResource(householdFile, parser, delimiter)

    this.addStep(
        PrepareCsvStep(
            name = "load household csv",
            csv=resource,
            repository = context.householdRepository
        )
    )
}

fun <S, C> S.finishHouseholds() where S: ModelExecution<C>, C: HouseholdContext {
    this.addStep(BuildStep("finish households", context.householdRepository))
}

fun <S, C> S.loadHouseholds() where S: ModelExecution<C>, C: Context, C: LegacyZonesContext, C: HouseholdContext {
    this.prepareHouseholds()
    this.finishHouseholds()
}
