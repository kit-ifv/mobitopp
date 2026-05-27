package domain.synthesis.behavior.economicstatus

import de.siegmar.fastcsv.reader.CsvReader
import domain.synthesis.attributes.household.HasIncome
import domain.synthesis.attributes.household.numberOfAdults
import domain.synthesis.attributes.household.numberOfMinors
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.data.EconomicStatus
import edu.kit.ifv.units.ClosedCurrencyRange
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.euros
import processor.builder.splitOnce
import java.io.InputStream
import java.nio.file.Path
import java.util.TreeMap
import kotlin.io.path.inputStream

/**
 * The default implementation to determine an Economic status for a household. Checks against a table of
 * people, based on the number of children and adults and then returns the economic status based on size and
 * income.
 */
class OECDAssigner(val oecdTranslation: (Double, Currency) -> EconomicStatus) :
    DetermineEconomicStatus<HasIncome, MinimumPersonAttributes> {
    override fun determineStatus(
        surveyHousehold: MinimalistHousehold<HasIncome, MinimumPersonAttributes>,
    ): EconomicStatus {
        val oecdNumber = calculateOECDAmount(surveyHousehold)
        val economicStatus = oecdTranslation(oecdNumber, surveyHousehold.attributes.income)
        return economicStatus
    }

    @Suppress(
        "MagicNumber",
    ) // In this case I understand the complaint of detekt, these numbers, 1.0, 0.5 and 0.3 are magic
    private fun calculateOECDAmount(surveyHousehold: MinimalistHousehold<HasIncome, MinimumPersonAttributes>): Double {
        val adults = surveyHousehold.numberOfAdults
        val additionalAdults = (adults - 1).coerceAtLeast(0)
        return 1.0 + 0.5 * additionalAdults + 0.3 * surveyHousehold.numberOfMinors
    }

    companion object {

        private class FileEntry(val amount: Double, val intervals: List<Pair<ClosedCurrencyRange, EconomicStatus>>)

        fun default(): OECDAssigner {
            val inputStream = OECDAssigner::class.java
                .getResourceAsStream("/economical-status-oecd2017.csv")
                ?: error("Resource not found: economical-status-oecd2017.csv")

            return fromInputStream(inputStream)
        }

        fun fromInputStream(inputStream: InputStream): OECDAssigner {
            val csvReader = CsvReader.builder().fieldSeparator(';').ofNamedCsvRecord(inputStream)
            val readContent = csvReader.map {
                FileEntry(
                    it.getField("household_size").replace(",", ".").toDouble(),
                    it.header.drop(1).map { f ->
                        headerToRange(f) to EconomicStatus.decode(it.getField(f).toInt())
                    },
                )
            }
            val map = TreeMap(readContent.associate { it.amount to it.intervals })
            return OECDAssigner { numPeep, income ->
                val mapping = map.floorEntry(numPeep).value
                mapping.firstOrNull { income in it.first }?.second
                    ?: throw NoSuchElementException("There is no matching economic status for an income of $income")
            }
        }
        fun fromPath(path: Path = Path.of("src/main/resources/economical-status-oecd2017.csv")): OECDAssigner =
            fromInputStream(path.inputStream())

        private fun headerToRange(input: String): ClosedCurrencyRange {
            val (start, end) = input.splitOnce(":").second.splitOnce("-")
            val endCurrency = if (end.isEmpty()) {
                Long.MAX_VALUE.euros
            } else {
                end.toCurrency()
            }
            return start.toCurrency()..endCurrency
        }

        private fun String.toCurrency(): Currency = toDouble().euros
    }
}
