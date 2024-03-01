//package usecases
//
//import CodePlan
//import domain.data.EconomicStatus
//import domain.data.HouseholdDataBuilder
//import domain.data.ZoneData
//import domain.location.RoadPosition
//import domain.location.parseRoadPosition
//import modeling.synthesis.BaseContext
//import modeling.synthesis.CsvResource
//import modeling.synthesis.Synthesis
//import utils.csv.CsvParser
//import utils.csv.DefaultRowCsvParser
//import utils.csv.SEMICOLON
//import utils.csv.currency
//import utils.csv.decode
//import utils.csv.id
//import utils.csv.int
//import utils.csv.long
//import utils.units.CurrencyUnits
//import java.io.File
//
//
//fun <S, C> S.loadHouseholdCsv(
//    parser: (Synthesis<C>) -> CsvParser<HouseholdDataBuilder>,
//    file: File? = null,
//    delimiter: String = SEMICOLON
//) where S: Synthesis<C>, C: BaseContext {
//
//    val householdFile = file ?: File(this.context.demandFolder.path + "\\demand-data\\household.csv")
//
//    this.addIdResource(householdFile.name, CsvResource(householdFile, parser(this), delimiter)) {
//            c, r -> c.householdBuilders = r
//    }
//}
//
//@Suppress("LongParameterList")
//fun <C: BaseContext> householdParser(
//    hhNumberColumn: String = "householdNumber",
//    yearColumn: String = "year",
//    zoneColumn: String = "homeZone",
//    locationColumn: String = "homeLocation",
//    roadPositionParser: (String) -> RoadPosition = String::parseRoadPosition,
//    domCodeColumn: String = "domCode",
//    typeColumn: String = "type",
//    incomeColumn: String = "income", //TODO unit: currency over time
//    incomeUnit: CurrencyUnits = CurrencyUnits.EUROS,
//    economicalStatusColumn: String = "economicalStatus",
//    economicalStatusCodes: CodePlan<EconomicStatus>? = null
//) : (Synthesis<C>) -> CsvParser<HouseholdDataBuilder> = { synthesis ->
//
//    val zoneRepo = { synthesis.context.zones } //TODO access to zones must be lazy
//    val economicalStatusCodePlan = economicalStatusCodes ?: synthesis.context.economicalStatusCodes
//
//    DefaultRowCsvParser { row ->
//        HouseholdDataBuilder(
//            householdNumber = row.long()[hhNumberColumn],
//            surveyYear = row.int()[yearColumn],
//            homeZone = zoneRepo().getById(row.id<ZoneData>()[zoneColumn]),
//            roadPosition = row(locationColumn, roadPositionParser),
//            domCode = row.int()[domCodeColumn],
//            type = row.int()[typeColumn],
//            incomePerMonth = row.int().currency(incomeUnit)[incomeColumn],
//            economicStatus = row.decode(economicalStatusCodePlan)[economicalStatusColumn]
//        )
//    }
//
//}
