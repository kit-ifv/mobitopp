package edu.kit.ifv.domain.synthesis.behavior.activitygeneration.children

import edu.kit.ifv.domain.jackson.standardCSVParse
import edu.kit.ifv.domain.shared.enums.ActivityType
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.inputStream
import kotlin.random.Random

class ChildActivityScheduleCsvReader {

    fun read(path: Path, random: Random, decoder: (String) -> ActivityType) = read(path.inputStream(), random, decoder)

    fun read(
        inputStream: InputStream,
        random: Random,
        decoder: (String) -> ActivityType,
    ): ChildActivityGeneratorSelector {
        val records = standardCSVParse<ChildActivityCsvRecord>(
            input = inputStream,
            separator = ',',
        )
        val activityPlans = records
            .groupBy { it.personId }
            .values
            .map { personRecords ->
                val firstRecord = personRecords.first()

                ChildActivitySchedule(
                    ageGroup = firstRecord.ageGroup,
                    dayType = firstRecord.dayType,
                    activityPlan = personRecords.map { it.toActivity(decoder) },
                )
            }

        fun generatorFor(ageGroup: AgeGroup) = Spawner.fromListOfPotentialPlans(random, activityPlans) {
            it.ageGroup == ageGroup
        }

        return ChildActivityGeneratorSelector(
            zeroToTwoYears = generatorFor(AgeGroup.ZERO_TO_TWO),
            threeToFiveYears = generatorFor(AgeGroup.THREE_TO_FIVE),
            sixToNineYears = generatorFor(AgeGroup.SIX_TO_NINE),
        )
    }

    fun readBundled(random: Random, decoder: (String) -> ActivityType): ChildActivityGeneratorSelector {
        val resource = "activities_children.csv"

        val input = requireNotNull(
            ChildActivityScheduleCsvReader::class.java.getResourceAsStream(resource),
        ) {
            "Missing bundled resource: $resource"
        }

        return input.use {
            read(it, random, decoder)
        }
    }
}
