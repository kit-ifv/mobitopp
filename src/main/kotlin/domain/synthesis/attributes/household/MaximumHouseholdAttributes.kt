package domain.synthesis.attributes.household

interface MaximumHouseholdAttributes : MinimumHouseholdAttributes, HasMutableNumberOfCars, HasMutableEconomicStatus {
    fun copy(): MaximumHouseholdAttributes {
        return MaximumHouseholdAttributesImpl(
            income = this.income,
            type = this.type,
            householdSize = -1, // Useless Attribute
            year = 2042,
            areaTypeCode = -1,
            amountOfCars = this.amountOfCars,
            location = this.location,
            economicStatus = this.economicStatus
        )
    }
}
