package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class MaleAge extends NamedAttribute implements Attribute {

	public MaleAge(
			final RegionalContext context, final AttributeType attributeType, final int lowerBound,
			final int upperBound, final int requestedWeight) {
		super(context, attributeType, lowerBound, upperBound, requestedWeight);
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

}
