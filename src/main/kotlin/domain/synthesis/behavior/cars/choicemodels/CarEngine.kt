package domain.synthesis.behavior.cars.choicemodels

import domain.shared.car.engine.EngineType
import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.HasNumberOfCars
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MaximumPersonAttributes

fun <X> EngineType.toAlternative(
    person: MaximumPersonAttributes,
    household: SynthesisHousehold<X, MaximumPersonAttributes>,
): EngineAlternative where X : MinimumHouseholdAttributes, X : HasNumberOfCars = EngineAlternative.fromHousehold(
    person,
    household,
)
