package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class PersonAttribute extends NamedAttribute implements Attribute {

	private final Function<PersonOfPanelData, Integer> personValue;

	public PersonAttribute(
			final RegionalContext context, final AttributeType attributeType, final int lowerBound,
			final int upperBound, final Function<PersonOfPanelData, Integer> personValue) {
		super(context, attributeType, lowerBound, upperBound);
		this.personValue = personValue;
	}

	@Override
	public Constraint createConstraint(final Demography demography) {
		int requestedWeight = demography.getDistribution(type).amount(lowerBound);
		return new PersonConstraint(requestedWeight, name());
	}

	@Override
	public int valueFor(
			final HouseholdOfPanelData household, final PanelDataRepository panelDataRepository) {
		List<PersonOfPanelData> persons = panelDataRepository.getPersonsOfHousehold(household.id());
		return Math
				.toIntExact(persons
						.stream()
						.filter(person -> personValue.apply(person) >= lowerBound)
						.filter(person -> personValue.apply(person) <= upperBound)
						.count());
	}

}
