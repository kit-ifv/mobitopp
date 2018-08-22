package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class PersonConstraintTest {

	private static final double margin = 1e-6;
	private static final int requestedWeight = 2;
	private static final int availablePeople = 2;
	private static final double initialWeight = 1.0d;
	private static final int noPeopleAvailable = 0;

	private PersonConstraint constraint;
	private Household household;

	@Before
	public void initialise() {
		household = newHousehold(availablePeople);
		constraint = new PersonConstraint(requestedWeight, h -> h.personAttribute("attribute"));
	}

	private Household newHousehold(int people) {
		Map<String, Integer> householdAttributes = emptyMap();
		Map<String, Integer> personAttributes = singletonMap("attribute", people);
		HouseholdOfPanelDataId id = new HouseholdOfPanelDataId(2000, 1);
		return new Household(id, initialWeight, householdAttributes, personAttributes);
	}

	@Test
	public void multipliesWeightWithPersons() {
		double weight = constraint.totalWeight(household);

		assertThat(weight, is(closeTo(availablePeople * initialWeight, margin)));
	}

	@Test
	public void filtersHouseholdsWithoutPeople() {
		Household householdWithoutPeople = newHousehold(noPeopleAvailable);
		
		boolean result = constraint.matches(householdWithoutPeople);

		assertFalse(result);
	}
}
