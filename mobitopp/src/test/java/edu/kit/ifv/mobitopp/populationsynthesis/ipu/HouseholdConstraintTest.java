package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import nl.jqno.equalsverifier.EqualsVerifier;

public class HouseholdConstraintTest {

	private static final String attribute = "attribute";
	private static final double margin = 1e-6;
	private static final int requestedWeight = 2;
	private static final int availablePeople = 2;
	private static final double initialWeight = 1.0d;
	private static final int noPeopleAvailable = 0;

	private HouseholdConstraint constraint;
	private WeightedHousehold household;

	@Before
	public void initialise() {
		household = newHousehold(availablePeople);
		constraint = new HouseholdConstraint(requestedWeight, attribute);
	}

	private WeightedHousehold newHousehold(int people) {
		Map<String, Integer> attributes = singletonMap(attribute, people);
		short year = 2000;
		HouseholdOfPanelDataId id = new HouseholdOfPanelDataId(year, 1);
		return new WeightedHousehold(id, initialWeight, attributes);
	}

	@Test
	public void doesNotConsiderValueOfAttributeForTotalWeight() {
		double weight = constraint.totalWeight(household);

		assertThat(weight, is(closeTo(initialWeight, margin)));
	}

	@Test
	public void filtersHouseholdsWithoutPeople() {
		WeightedHousehold householdWithoutPeople = newHousehold(noPeopleAvailable);

		boolean result = constraint.matches(householdWithoutPeople);

		assertFalse(result);
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(HouseholdConstraint.class).usingGetClass().verify();
	}
}
