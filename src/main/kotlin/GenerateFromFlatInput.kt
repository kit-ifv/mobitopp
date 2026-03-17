import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.RawSurveyInfo
import domain.synthesis.behavior.SmallestSurveyPerson
import domain.synthesis.behavior.SurveyHousehold
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicInteger

class GenerateFromFlatInput<X, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
    private val dataInput: Collection<X>,
    private val idExtractor: (X) -> Long,
    private val householdDataExtractor: (List<X>) -> S,
    private val personDataExtractor: (X) -> T,
) : GenerateHouseholds<S, T> {
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
                        personDataExtractor(it)
                    )
                },
                attributes = householdDataExtractor(data)
            )


        }

    }


    companion object {
        fun standard(input: Collection<RawSurveyInfo>):
                GenerateFromFlatInput<
                        RawSurveyInfo,
                        MaximumHouseholdAttributes,
                        MaximumPersonAttributes,
                        > {

            return GenerateFromFlatInput(
                input, idExtractor = { it.householdId },
                householdDataExtractor = {
                    val data = it.first() // The flat format means that the data is repeated multiple times.
                    MaximumHouseholdAttributes(
                        income = data.householdIncome,
                        type = data.type,
                        householdSize = data.householdSize,
                        year = data.year,
                        areaTypeCode = data.areaType,
                        numCars = data.cars
                    )
                },
                personDataExtractor = {
                    MaximumPersonAttributes(
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
                }
            )
        }

        fun fromPath(path: Path) = standard(parseSurvey(path).toList())
        fun fromPath(fileString: String) = fromPath(Path.of(fileString))
    }
}

