package domain.synthesis.behavior.transitpass

import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.EnumeratedDiscreteModelBuilder
import kotlin.random.Random

@Suppress("SpacingAroundColon")
class AssignByDiscreteChoice(
    val model: FixedChoiceModel<Boolean, TicketCharacteristics> =
        transitPassChoiceModel.build(YesTransitPass).fixed(setOf(true, false)),
) : AssignTransitCardOwnership<MaximumHouseholdAttributes, MaximumPersonAttributes> {

    constructor(
        parameters: TransitPassParameters,
        model: EnumeratedDiscreteModelBuilder<Boolean, TicketCharacteristics, TransitPassParameters> =
            transitPassChoiceModel,
    ) : this(model.build(parameters))

    context(household: ISurveyHousehold<MaximumHouseholdAttributes, MaximumPersonAttributes>)
    override fun assignForPerson(person: SurveyPerson<MaximumPersonAttributes>): Boolean =
        context(TicketCharacteristics(household, person), Random(person.personId)) {
            model.select()
        }
}
