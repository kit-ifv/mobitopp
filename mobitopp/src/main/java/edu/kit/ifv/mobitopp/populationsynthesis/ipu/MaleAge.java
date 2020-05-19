package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class MaleAge extends NamedAttribute implements Attribute {

	public MaleAge(
			final AttributeContext context, final AttributeType attributeType, final int lowerBound,
			final int upperBound) {
		super(context, attributeType, lowerBound, upperBound);
	}

	@Override
	public int valueFor(HouseholdOfPanelData household, PanelDataRepository panelDataRepository) {
		List<PersonOfPanelData> persons = panelDataRepository.getPersonsOfHousehold(household.id());
		return valueFor(persons);
	}

	private int valueFor(List<PersonOfPanelData> persons) {
		return Math
				.toIntExact(persons
						.stream()
						.filter(person -> person.age() >= lowerBound)
						.filter(person -> person.age() <= upperBound)
						.filter(person -> Gender.MALE.equals(person.gender()))
						.count());
	}

	@Override
	public Constraint createConstraint(Demography demography) {
		int requestedWeight = demography.maleAge().amount(lowerBound);
		return new PersonConstraint(requestedWeight, name());
	}

}
