package domain.synthesis.behavior.cars.choicemodels

/**
 * The class [CarOwnershipAttributes] is an example to show the attributes that are required to form a meaningful decision
 * in the [carAmountChoiceModel] discrete choice model. In old mobitopp the attributes would have been a helper function written
 * in all caps like "HOUSEHOLD_SIZE". Here the attributes are encapsulated in the specific discrete choice model.
 *
 * Note that the approach to create the attributes class can be implemented in multiple different ways. In this concrete
 * example we delegate all necessary factors into an own object infos of type [CarOwnershipFactors] and extract the
 * attributes that we desire by writing (val: attribute = infos.attribute). There exist multiple different ways to
 * build the Attributes object and this approach is not the definite answer to always create attributes for an utility
 * function.
 *
 * The benefit of creating individual properties instead of methods for the utility function is readability.
 * it.size == 1 reads more fluent
 * than writing it.size() == 1 (writing as a method) and much more fluent than writing it.household.members.count() == 1
 * (writing as code).
 *
 *
 * @property size This helper attribute returns the amount of people in the household as an integer.
 * @property economicStatus This helper attribute returns the assigned economic status [domain.synthesis.data.household.EconomicStatus] of the household.
 * @property amountOfChildren This helper attribute returns the number of agents in the household that are of age [0, 10)
 * @property amountOfYouth This helper attribute returns the number of agents in the household that are of age [10, 17]
 * @property amountOfWorkers This helper attribute returns the number of agents in the household that are considered
 * "Working" by the [domain.synthesis.behavior.cars.ownership.EmploymentSorter]. The default logic is that agents with [domain.synthesis.data.person.Employment.FULLTIME] and [domain.synthesis.data.person.Employment.PARTTIME] are
 * considered "working". You can override this behaviour by passing a different [domain.synthesis.behavior.cars.ownership.EmploymentSorter] to the [CarOwnershipFactors]
 * creation.
 * @property amountOfLicences This helper attribute returns the number of agents in the household that have a driving licence
 * @property isWg determines whether the selected household is a flat, as in at least 3 or more students and only students,
 * based on the [domain.synthesis.behavior.cars.ownership.EmploymentSorter] logic to determine what employment type qualifies as student (Default is [domain.synthesis.data.person.Employment.STUDENT_TERTIARY])
 * @property isOnlyRetired This helper attribute returns whether all agents in the household are retired, based on the [domain.synthesis.behavior.cars.ownership.EmploymentSorter]
 * to determine retirement.
 *  @property isOnlyUnemployed This helper attribute returns whether all agents in the household are unemployed, based on the [domain.synthesis.behavior.cars.ownership.EmploymentSorter]
 *  to determine unemployment.
 */
class CarOwnershipAttributes(infos: CarOwnershipFactors) {
    val randomNumber = 0.0
    val size = infos.size
    val economicStatus = infos.economicStatus
    val amountOfChildren = infos.amountOfChildren
    val amountOfYouth = infos.amountOfYouth
    val amountOfWorkers = infos.numberOfWorkers
    val amountOfLicences = infos.numDrivingLicence
    val isWg = infos.isWg
    val isOnlyRetired = infos.isOnlyRetired
    val isOnlyUnemployed = infos.isOnlyUnemployed
}
