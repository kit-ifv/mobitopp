package domain.synthesis.behavior

import domain.synthesis.attributes.household.HasIncome
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.HasAge
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.data.EconomicStatus
import edu.kit.ifv.units.ClosedCurrencyRange
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.euros
import processor.builder.splitOnce
import utils.csv.DefaultCsvParser
import java.nio.file.Path
import java.util.TreeMap

/**
 * Assign an economic status to a household
 */
fun interface DetermineEconomicStatus<in S, in T> {
    fun determineStatus(surveyHousehold: MinimalistHousehold<S, T>): EconomicStatus
}

class AlwaysAssignSameStatus(val economicStatus: EconomicStatus) : DetermineEconomicStatus<Any?, Any?> {
    override fun determineStatus(surveyHousehold: MinimalistHousehold<*, *>): EconomicStatus {
        return economicStatus
    }
}

/**
 * The default implementation to determine an Economic status for a household. Checks against a table of
 * people, based on the number of children and adults and then returns the economic status based on size and
 * income.
 */
class OECDAssigner(val oecdTranslation: (Double, Currency) -> EconomicStatus) :
    DetermineEconomicStatus<HasIncome, MinimumPersonAttributes> {
    override fun determineStatus(surveyHousehold: MinimalistHousehold<HasIncome, MinimumPersonAttributes>): EconomicStatus {
        val oecdNumber = calculateOECDAmount(surveyHousehold)
        val economicStatus = oecdTranslation(oecdNumber, surveyHousehold.attributes.income)
        return economicStatus
    }

    @Suppress(
        "MagicNumber"
    ) // In this case I understand the complaint of detekt, these numbers, 1.0, 0.5 and 0.3 are magic
    private fun calculateOECDAmount(surveyHousehold: MinimalistHousehold<HasIncome, MinimumPersonAttributes>): Double {
        val adults = surveyHousehold.numberOfAdults
        val additionalAdults = (adults - 1).coerceAtLeast(0)
        return 1.0 + 0.5 * additionalAdults + 0.3 * surveyHousehold.numberOfMinors
    }

    companion object {
        fun fromPath(
            path: Path = Path.of("src/main/resources/economical-status-oecd2017.csv")
        ): OECDAssigner {
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
val MinimalistHousehold<*, HasAge>.numberOfAdults get() = members.count { it.attributes.age >= 18 }

@Suppress("MagicNumber") // These magic numbers are ok
val MinimalistHousehold<*, HasAge>.numberOfMinors get() = members.count { it.attributes.age < 18 }

private class FileEntry(
    val amount: Double,
    val intervals: List<Pair<ClosedCurrencyRange, EconomicStatus>>
)
