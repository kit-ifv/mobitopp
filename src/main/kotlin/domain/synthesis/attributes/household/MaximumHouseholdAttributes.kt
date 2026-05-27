package domain.synthesis.attributes.household

/**
 * Defines the most complete set of household attributes currently required by the core framework.
 *
 * A household attribute class that implements this interface provides all attributes needed by the
 * prefabricated implementations in this code base. In other words, if a project-specific household
 * attribute type fulfills this interface, it can be used with every existing core-framework
 * implementation that depends on household attributes.
 *
 * When new core-framework functionality requires additional household attributes, this interface
 * should be updated accordingly so that it continues to represent the full attribute set expected
 * by the framework.
 */
interface MaximumHouseholdAttributes :
    MinimumHouseholdAttributes,
    HasMutableNumberOfCars,
    HasMutableEconomicStatus {
    fun copy(): MaximumHouseholdAttributes = MaximumHouseholdAttributesImpl(
        income = this.income,
        type = this.type,
        householdSize = -1, // Useless Attribute
        year = 2042,
        areaTypeCode = -1,
        amountOfCars = this.amountOfCars,
        location = this.location,
        economicStatus = this.economicStatus,
    )
}
