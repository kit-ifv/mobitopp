package edu.kit.ifv.domain.synthesis.results.legacy
import edu.kit.ifv.domain.synthesis.behavior.cars.SynthesisCar
import edu.kit.ifv.domain.synthesis.results.CSVOutput

@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object LegacyCarOutput : CSVOutput<SynthesisCar> {
    override val header: List<String> = listOf(
        "ownerId",
        "mainUserId",
        "personalUserId",
        "carType",
        "car attributes",
    )

    @Suppress("MagicNumber")
    override fun convert(element: SynthesisCar): String = element.run {
        toCSV(
            mainUser?.householdId ?: "Null",
            mainUser?.personId ?: "-1",
            mainUser?.personId ?: "-1",
            this.engine.type.asText,
            id,
            "0", // TODO verify that this is acurraty
            mainUser?.homeLocation ?: "Null",
            segment,
            seats,
            0.0, // TODO this appears to be a fixed value.
            1.0, // TODO this appears to be a fixed value.
            1000.0, // TODO this appears to be a fixed value.

        )
    }
}
