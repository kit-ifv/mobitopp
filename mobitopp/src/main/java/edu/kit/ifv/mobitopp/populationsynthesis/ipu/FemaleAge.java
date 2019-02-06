package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Constraint;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.PersonConstraint;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class FemaleAge implements Attribute {

	private final String prefix;
	private final int lowerBound;
	private final int upperBound;

	public FemaleAge(String prefix, int lowerBound, int upperBound) {
		super();
		this.prefix = prefix;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
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
						.filter(person -> Gender.FEMALE.equals(person.gender()))
						.count());
	}

	@Override
	public String name() {
		return prefix + lowerBound + "-" + upperBound;
	}

	@Override
	public Constraint createConstraint(Demography demography) {
		RangeDistributionItem item = demography.femaleAge().getItem(lowerBound);
		int requestedWeight = item.amount();
		return new PersonConstraint(requestedWeight, name());
	}

}
