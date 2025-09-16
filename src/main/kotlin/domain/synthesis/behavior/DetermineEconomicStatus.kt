package domain.synthesis.behavior

import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.data.EconomicStatus
import edu.kit.ifv.units.ClosedCurrencyRange
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.euros
import processor.builder.splitOnce
import utils.csv.DefaultCsvParser
import java.nio.file.Path
import java.util.*

/**
 * Assign an economic status to a household
 */
fun interface DetermineEconomicStatus<T> {
    fun determineStatus(surveyHousehold: SynthesisHousehold<out T>): EconomicStatus
}

class AlwaysAssignSameStatus(val economicStatus: EconomicStatus) : DetermineEconomicStatus<Any> {
    override fun determineStatus(surveyHousehold: SynthesisHousehold<out Any>): EconomicStatus {
        return economicStatus
    }
}

/**
 * The default implementation to determine an Economic status for a household. Checks against a table of
 * people, based on the number of children and adults and then returns the economic status based on size and
 * income.
 */
class OECDAssigner<T : SurveyInfo>(val oecdTranslation: (Double, Currency) -> EconomicStatus) :
    DetermineEconomicStatus<T> {
    override fun determineStatus(surveyHousehold: SynthesisHousehold<out T>): EconomicStatus {
        val oecdNumber = calculateOECDAmount(surveyHousehold)
        val economicStatus = oecdTranslation(oecdNumber, surveyHousehold.income)
        return economicStatus
    }

    @Suppress(
        "MagicNumber"
    ) // In this case I understand the complaint of detekt, these numbers, 1.0, 0.5 and 0.3 are magic
    private fun calculateOECDAmount(surveyHousehold: SynthesisHousehold<out T>): Double {
        val adults = surveyHousehold.numberOfAdults
        val additionalAdults = (adults - 1).coerceAtLeast(0)
        return 1.0 + 0.5 * additionalAdults + 0.3 * surveyHousehold.numberOfMinors
    }

    companion object {
        fun <T : SurveyInfo> fromPath(
            path: Path = Path.of("src/test/resources/synthesis/economical-status-oecd2017.csv")
        ): OECDAssigner<T> {
            val parser = DefaultCsvParser { row ->
                FileEntry(

                    amount = row("household_size") { it.replace(",", ".").toDouble() },
                    (1..<row.size).map {
                        val header = row.headerForIndex(it)
                        headerToRange(header) to EconomicStatus.decode(row.valueAt(it).toInt())
                    }
                )
            }

            val map = TreeMap(parser.parse(path).associate { it.amount to it.intervals })
            return OECDAssigner { numPeep, income ->
                val mapping = map.floorEntry(numPeep).value
                mapping.first { income in it.first }.second
            }
        }

        private fun headerToRange(input: String): ClosedCurrencyRange {
            val (start, end) = input.splitOnce(":").second.splitOnce("-")
            val endCurrency = if (end.isEmpty()) {
                Long.MAX_VALUE.euros
            } else {
                end.toCurrency()
            }
            return start.toCurrency()..endCurrency
        }

        private fun String.toCurrency(): Currency {
            return toDouble().euros
        }
    }
}

@Suppress("MagicNumber") // These magic numbers are ok
val SynthesisHousehold<out SurveyAge>.numberOfAdults get() = members.count { it.age >= 18 }

@Suppress("MagicNumber") // These magic numbers are ok
val SynthesisHousehold<out SurveyAge>.numberOfMinors get() = members.count { it.age < 18 }

private class FileEntry(
    val amount: Double,
    val intervals: List<Pair<ClosedCurrencyRange, EconomicStatus>>
)
