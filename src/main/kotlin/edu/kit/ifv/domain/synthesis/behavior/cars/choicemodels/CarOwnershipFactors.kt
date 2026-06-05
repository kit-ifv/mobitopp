package edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus
import edu.kit.ifv.domain.synthesis.attributes.household.HasEconomicStatus
import edu.kit.ifv.domain.synthesis.attributes.person.MaximumPersonAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.employment
import edu.kit.ifv.domain.synthesis.attributes.person.hasLicence
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold
import edu.kit.ifv.domain.synthesis.behavior.cars.ownership.DefaultEmploymentSorter
import edu.kit.ifv.domain.synthesis.behavior.cars.ownership.EmploymentSorter
import kotlin.random.Random

/**
 * This class calculates the necessary attributes for the calculation of car ownership using a [domain.synthesis.SynthesisHousehold]
 * from the population synthesis as input, as well as an [domain.synthesis.behavior.cars.ownership.EmploymentSorter] as dependency injection for translating
 * employment types.
 */
@Suppress("MagicNumber") // These magic numbers are ok
class CarOwnershipFactors(
    val household: MinimalistHousehold<HasEconomicStatus, MaximumPersonAttributes>,
    employmentSorter: EmploymentSorter = DefaultEmploymentSorter,
) {
    val size = household.members.size
    val economicStatus: EconomicStatus = household.attributes.economicStatus
    val numDrivingLicence: Int = household.members.count { it.hasLicence }
    val numberOfWorkers = household.members.count { employmentSorter.isWorking(it.employment) }
    val isWg = household.members.all { employmentSorter.isUniversityStudent(it.employment) } && size >= 3
    val isOnlyRetired = household.members.all { employmentSorter.isRetired(it.employment) }
    val isOnlyUnemployed = household.members.all { employmentSorter.isUnemployed(it.employment) }
    val amountOfChildren = household.members.count { it.attributes.age < 10 }
    val amountOfYouth = household.members.count { it.attributes.age in 10..17 }

    val random: Random = Random.Default
}
