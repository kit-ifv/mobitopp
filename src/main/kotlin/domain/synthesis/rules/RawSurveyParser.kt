package domain.synthesis.rules

import domain.shared.enums.person.Employment
import domain.shared.enums.person.Sex
import domain.synthesis.behavior.RawSurveyInfo
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.kilometers
import edu.kit.ifv.units.toCurrency
import utils.csv.DefaultCsvParser
import utils.csv.Row
import java.nio.file.Path

/**
 * Used to read the original synthesis rule target file format.
 */
object RawSurveyParser {
    fun parseSurvey(path: Path, surveyColumns: SurveyColumns = SurveyColumns()): Sequence<RawSurveyInfo> {
        val parser = DefaultCsvParser { row ->
            readRawSurveyInfo(row, surveyColumns)
        }

        return parser.parse(path)
    }

    fun parseSurvey(path: Path, lambda: SurveyColumns.() -> Unit): List<RawSurveyInfo> {
        val surveyColumns = SurveyColumns()
        surveyColumns.apply(lambda)
        return parseSurvey(path, surveyColumns).toList()
    }

    fun readRawSurveyInfo(row: Row, surveyColumns: SurveyColumns): RawSurveyInfo = RawSurveyInfo(
        householdId = row(surveyColumns.id).toLong(),
        year = row(surveyColumns.year).toInt(),
        areaType = row(surveyColumns.areatype).toInt(),
        householdSize = row(surveyColumns.size).toInt(),
        personNumber = row(surveyColumns.personnumber).toInt(),
        sex = row(surveyColumns.sex) { Sex.decode(it.toInt()) },
        birthyear = row(surveyColumns.birthyear).toInt(),
        employment = row(surveyColumns.employmenttype) { Employment.decode(it.toInt()) },
        hasCommuterTicket = row(surveyColumns.commuterticket).toBooleanNumeric(),
        householdIncome = row(surveyColumns.hhincome) { it.toDouble().toCurrency(CurrencyUnit.EUROS) },
        householdIncomeClass = row(surveyColumns.hhincomeClass).toInt(),
        typeCode = row(surveyColumns.type).toInt(),
        cars = row(surveyColumns.cars).toInt(),
        hasBicycle = row(surveyColumns.bicycle).toBooleanNumeric(),
        hasLicence = row(surveyColumns.licence).toBooleanNumeric(),
        distanceWork = row(surveyColumns.distanceWork) { it.toDouble().kilometers },
        distanceEducation = row(surveyColumns.distanceEducation) { it.toDouble().kilometers },
    )
    data class SurveyColumns(
        var id: String = "ID",
        var year: String = "year",
        var areatype: String = "areatype",
        var size: String = "size",
        var personnumber: String = "personnumber",
        var sex: String = "sex",
        var birthyear: String = "birthyear",
        var employmenttype: String = "employmenttype",
        var commuterticket: String = "commuterticket",
        var hhincome: String = "hhincome",
        var hhincomeClass: String = "hhincome_class",
        var type: String = "type",
        var cars: String = "cars",
        var bicycle: String = "bicycle",
        var licence: String = "licence",
        var distanceWork: String = "distance_work",
        var distanceEducation: String = "distance_education",
    )
    private fun String.toBooleanNumeric(): Boolean = when (this) {
        "1" -> true

        "0" -> false

        "-1" -> false
        else -> throw IllegalArgumentException("Invalid binary string for Boolean conversion: $this")
    }

}