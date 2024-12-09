package synthesis

import domain.data.EconomicStatus
import splitOnce
import units.Currency
import units.euros
import utils.csv.DefaultCsvParser
import java.nio.file.Path
import java.util.*

/**
 * Assign an economic status to a household
 */
fun interface AssignEconomicStatus {
    fun assign(surveyHousehold: SynthesisHouseholdBuilder)
}

/**
 * The default implementation to determine an Economic status for a household. Checks against a table of
 * people, based on the number of children and adults and then returns the economic status based on size and
 * income.
 */
class OECDAssigner(val oecdTranslation: (Double, Currency) -> EconomicStatus) : AssignEconomicStatus {
    override fun assign(surveyHousehold: SynthesisHouseholdBuilder) {
        val oecdNumber = calculateOECDAmount(surveyHousehold)
        val economicStatus = oecdTranslation(oecdNumber, surveyHousehold.income)
        surveyHousehold.economicStatus = economicStatus

    }

    private fun calculateOECDAmount(surveyHousehold: SynthesisHouseholdBuilder): Double {
        val adults = surveyHousehold.numberOfAdults
        val additionalAdults = (adults - 1).coerceAtLeast(0)
        return 1.0 + 0.5 * additionalAdults + 0.3 * surveyHousehold.numberOfMinors
    }

    companion object {
        fun fromPath(path: Path = Path.of("src/test/resources/synthesis/economical-status-oecd2017.csv")): OECDAssigner {

            val parser = DefaultCsvParser { row ->
                FileEntry(

                    amount = row("household_size") { it.replace(",", ".").toDouble() },
                    (1..<row.size).map {
                        val header = row.headerForIndex(it)
                        headerToRange(header) to EconomicStatus.decode(row.valueAt(it).toInt())
                    }
                )


            }

            val map = TreeMap(parser.parse(path.toFile()).associate { it.amount to it.intervals })
            return OECDAssigner { numPeep, income ->
                val mapping = map.floorEntry(numPeep).value
                mapping.first { income in it.first }.second
            }
        }

        private fun headerToRange(input: String): ClosedRange<Currency> {
            val (start, end) = input.splitOnce(":").second.splitOnce("-")
            val endCurrency = if (end.isEmpty()) {
                Long.MAX_VALUE.euros
            } else end.toCurrency()
            return start.toCurrency()..endCurrency
        }

        private fun String.toCurrency(): Currency {
            return toDouble().euros
        }
    }

}

private class FileEntry(
    val amount: Double,
    val intervals: List<Pair<ClosedRange<Currency>, EconomicStatus>>
)