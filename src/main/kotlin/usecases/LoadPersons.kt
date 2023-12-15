package usecases

import CodePlan
import ID
import domain.data.EMobilityPersonDataBuilder
import domain.data.EconomicStatus
import domain.data.HouseholdDataBuilder
import domain.location.RoadPosition
import domain.location.parseRoadPosition
import modeling.synthesis.BaseContext
import modeling.synthesis.CsvResource
import modeling.synthesis.Synthesis
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.CsvParserBuilder
import utils.csv.SEMICOLON
import utils.csv.property
import utils.units.CurrencyUnits
import utils.units.toCurrency
import java.io.File


//fun <S, C> S.loadPersonCsv(
//    parser: (Synthesis<C>) -> CsvParser<EMobilityPersonDataBuilder>,
//    file: File? = null,
//    delimiter: String = SEMICOLON
//) where S: Synthesis<C>, C: BaseContext {
//
//    val personFile = file ?: File(this.context.demandFolder.path + "\\demand-data\\person.csv")
//
//    this.addIdResource(personFile.name, CsvResource(personFile, parser(this), delimiter)) {
//            c, r -> c.personBuilders = r
//    }
//}
//
//
//
//
//
//
//@Suppress("LongParameterList")
//fun <C: BaseContext> personParser(
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
//    val zoneRepo = { synthesis.context.zones } //TODO access to zones mut be lazy
//    val economicalStatusCodePlan = economicalStatusCodes ?: synthesis.context.economicalStatusCodes
//
//    CsvParserBuilder { HouseholdDataBuilder() }
//        .long.property(hhNumberColumn) { e, l -> e.householdNumber = l }
//        .int.property(yearColumn) { e, i -> e.surveyYear = i }
//        .long.property(zoneColumn) { e, l -> e.homeZone = zoneRepo().getById(ID(l.toULong())) }
//        .string.property(locationColumn) { e, s -> e.roadPosition = roadPositionParser(s) }
//        .int.property(domCodeColumn) { e, i -> e.domCode = i }
//        .int.property(typeColumn) { e, i -> e.type = i }
//        .int.property(incomeColumn) { e, i -> e.incomePerMonth = i.toCurrency(incomeUnit) }
//        .int.property(economicalStatusColumn) { e, i -> e.economicStatus = economicalStatusCodePlan.decode(i) }
//        .onErrorUse(ErrorHandling.WARN_DROP)
//        .build()
//
//}
