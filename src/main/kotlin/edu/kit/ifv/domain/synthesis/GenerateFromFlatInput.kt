package edu.kit.ifv.domain.synthesis
import edu.kit.ifv.domain.synthesis.attributes.household.MaximumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.household.MaximumHouseholdAttributesImpl
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MaximumPersonAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MaximumPersonAttributesImpl
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.behavior.RawSurveyInfo
import edu.kit.ifv.domain.synthesis.behavior.SmallestSurveyPerson
import edu.kit.ifv.domain.synthesis.behavior.SurveyHousehold
import edu.kit.ifv.domain.synthesis.rules.RawSurveyParser
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicInteger

/**
 * Generates survey households from flat input data, such as CSV rows in which household-level and
 * person-level attributes are stored together.
 *
 * This generator is intended for input formats where each row represents one person, while
 * household attributes may be repeated across all rows belonging to the same household. Rows are
 * grouped by the household id returned by [idExtractor]. For each group, one [ISurveyHousehold] is
 * created. Each row in that group becomes one survey person.
 *
 * The generic input type [X] represents one row of the source data. The household attribute type
 * [S] represents the project-specific household attributes created for each grouped household. The
 * person attribute type [T] represents the project-specific person attributes created for each row.
 *
 * Project-specific code is responsible for defining [X], [S], and [T], and for ensuring that
 * [idExtractor], [householdDataExtractor], and [personDataExtractor] correctly translate the source
 * data into framework-compatible household and person attributes. In particular, the caller is
 * responsible for validating that these extractor functions match the structure and semantics of
 * the input data.
 *
 * @param X the source row type. Each instance represents one person-level row in the flat input.
 * @param S the household attribute type created once per grouped household.
 * @param T the person attribute type created once per source row.
 * @property dataInput all source rows to convert into survey households.
 * @property idExtractor extracts the household id from a single source row. All rows with the same
 * household id are grouped into the same household.
 * @property householdDataExtractor creates the household attributes of type [S] from all rows
 * belonging to one household. Since household-level data may be repeated or encoded differently
 * depending on the input source, this function is responsible for resolving any ambiguity. For
 * example, it may select the first row, validate that all rows contain the same value, or aggregate
 * values across all household members.
 * @property personDataExtractor creates the person attributes of type [T] from a single source row.
 *
 * Person ids used by the generated survey persons are created internally as incremental ids. If the
 * source data contains an external person id that must be preserved for later comparison or tracing,
 * include that value in the project-specific person attribute type [T].
 */
class GenerateFromFlatInput<X, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
    private val dataInput: Collection<X>,
    private val idExtractor: (X) -> Long,
    private val householdDataExtractor: (List<X>) -> S,
    private val personDataExtractor: (X) -> T,
) : GenerateSurveyHouseholds<S, T> {
    private val idCounter = AtomicInteger(0)
    private fun getNextId() = idCounter.getAndIncrement()

    override fun generateSurveyHouseholds(): Collection<ISurveyHousehold<S, T>> {
        val groupedTargets = dataInput.groupBy { idExtractor(it) }

        return groupedTargets.map { (id, data) ->
            SurveyHousehold(
                surveyHouseholdId = id,
                members = data.map {
                    SmallestSurveyPerson(
                        getNextId(),
                        personDataExtractor(it),
                    )
                },
                attributes = householdDataExtractor(data),
            )
        }
    }

    companion object {
        fun standard(
            input: Collection<RawSurveyInfo>,
        ): GenerateFromFlatInput<
            RawSurveyInfo,
            MaximumHouseholdAttributes,
            MaximumPersonAttributes,
            > =
            GenerateFromFlatInput(
                input,
                idExtractor = { it.householdId },
                householdDataExtractor = {
                    val data = it.first() // The flat format means that the data is repeated multiple times.
                    MaximumHouseholdAttributesImpl(
                        income = data.householdIncome,
                        type = data.type,
                        householdSize = data.householdSize,
                        year = data.year,
                        areaTypeCode = data.areaType,
                        amountOfCars = data.cars,
                    )
                },
                personDataExtractor = {
                    MaximumPersonAttributesImpl(
                        age = it.age,
                        sex = it.sex,
                        distanceWork = it.distanceWork,
                        distanceEducation = it.distanceEducation,
                        employment = it.employment,
                        birthYear = it.birthyear,
                        personNumber = it.personNumber,
                        hasBicycle = it.hasBicycle,
                        hasLicence = it.hasLicence,
                    )
                },
            )

        fun fromPath(path: Path) = standard(RawSurveyParser.parseSurvey(path).toList())
        fun fromPath(fileString: String) = fromPath(Path.of(fileString))
    }
}
