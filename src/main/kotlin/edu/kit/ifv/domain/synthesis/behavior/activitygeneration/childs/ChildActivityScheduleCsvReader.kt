package edu.kit.ifv.domain.synthesis.behavior.activitygeneration.childs

import edu.kit.ifv.domain.jackson.standardCSVParse
import edu.kit.ifv.domain.shared.enums.ActivityType
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.inputStream
import kotlin.random.Random

class ChildActivityScheduleCsvReader {
    fun read(
        path: Path = DEFAULT_PATH,
        random: Random,
        decoder: (String) -> ActivityType,
    ): ChildActivityGeneratorSelector {
        val records = standardCSVParse<ChildActivityCsvRecord>(
            input = path.inputStream(),
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

    companion object {
        val DEFAULT_PATH =
            Path(
                "\\\\ifv-fs.ifv.kit.edu\\Forschung\\Projekte\\ALIKE_MOIA_Hochbahn_autonom\\Work\\Modelle\\Bevölkerung\\Activities\\children\\activities_children.csv",
            )
    }
}
