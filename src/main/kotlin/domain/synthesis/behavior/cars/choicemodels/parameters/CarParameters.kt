package domain.synthesis.behavior.cars.choicemodels.parameters

/**
 * For the purpose of demonstrating the translation capabilities we introduce this data class to bring structure in
 * the otherwise clustered [CarOwnershipParameters]. The idea is that the individual parameters of the large parameter
 * set can be extracted and grouped based on their corresponding option. The benefit is that we can design a shared
 * utility function [domain.synthesis.behavior.discreteChoice.standardFunction], which is fed with the correct parameter object [CarOwnershipParameters.oneCar] for
 * the option of one car, [CarOwnershipParameters.twoCar] for two cars, etc. Note that this translation approach allows
 * us to rename potentially misleading or unclear parameters to a more fitting name, as well as adding descriptions for
 * documentation purposes.
 *
 * @param oneMember this parameter is the factor by which the utility of a specific option should be shifted if the household has exactly one member.
 * @param twoMembers this parameter is the factor by which the utility of a specific option should be shifted if the household has exactly two members.
 * @param fourOrMoreMembers this parameter is the factor by which the utility  of a specific option should be shifted if the household has at least 4 members.
 * @param lowIncome this parameter is the factor by which the utility of a specific option should be shifted if the household income is considered "low".
 * @param highIncome this parameter is the factor by which the utility of a specific option should be shifted if the household income is considered "high".
 * @param childrenFactor this parameter is the factor by which the utility of a specific option should be shifted if the household has children below the age of 10.
 * @param youthFactor  this parameter is the factor by which the utility of a specific option should be shifted if the household has youths with an age between 10 and 17
 * @param oneLicence  this parameter is the factor by which the utility of a specific option should be shifted if the household has exactly one member with a driving licence
 * @param twoLicences  this parameter is the factor by which the utility of a specific option should be shifted if the household has exactly two members with a driving licence
 * @param threeLicences  this parameter is the factor by which the utility of a specific option should be shifted if the household has exactly three members with a driving licence
 * @param fourOrMoreLicences  this parameter is the factor by which the utility of a specific option should be shifted if the household has 4 or more members with a driving licence
 * @param isFlat  this parameter is the factor by which the utility of a specific option should be shifted if the household is a flat (Only students and at least 3 members)
 * @param isRetired  this parameter is the factor by which the utility of a specific option should be shifted if all members of the household are retired
 * @param isUnemployed  this parameter is the factor by which the utility of a specific option should be shifted if all members of the household are unemployed
 * @param mu the expected value of the normal distribution used for assigning this option to a household.
 * @param sigma the deviation of the normal distirubtion used for assigning this option to a household.
 */
data class CarParameters(
    val oneMember: Double,
    val twoMembers: Double,
    val fourOrMoreMembers: Double,
    val lowIncome: Double,
    val highIncome: Double,
    val childrenFactor: Double,
    val youthFactor: Double,
    val oneWorker: Double,
    val twoWorkers: Double,
    val oneLicence: Double,
    val twoLicences: Double,
    val threeLicences: Double,
    val fourOrMoreLicences: Double,
    val isFlat: Double,
    val isRetired: Double,
    val isUnemployed: Double,
    val mu: Double,
    val sigma: Double,
)