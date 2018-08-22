package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Test;

public class BaseConstraintTest {

	private static final double requestedWeight = 6.0d;

	@Test
	public void updateWeightsOnAllHousehold() {
		Household someHousehold = newHousehold(1, 1.0d);
		Household anotherHousehold = newHousehold(2, 2.0d);
		List<Household> households = asList(someHousehold, anotherHousehold);
		BaseConstraint constraint = newConstraint();

		List<Household> updatedHouseholds = constraint.update(households);

		Household updatedSomeHousehold = newHousehold(1, 2.0d);
		Household updatedAnotherHousehold = newHousehold(2, 4.0d);
		assertThat(updatedHouseholds,
				containsInAnyOrder(updatedSomeHousehold, updatedAnotherHousehold));
	}

	@Test
	public void updateWeightOnSingleHousehold() {
		Household someHousehold = newHousehold(1, 1.0d);
		Household anotherHousehold = newHousehold(2, 2.0d);
		List<Household> households = asList(someHousehold, anotherHousehold);
		BaseConstraint constraint = newConstraint(onlySecondHousehold());

		List<Household> updatedHouseholds = constraint.update(households);

		Household updatedSomeHousehold = newHousehold(1, 1.0d);
		Household updatedAnotherHousehold = newHousehold(2, 6.0d);
		assertThat(updatedHouseholds,
				containsInAnyOrder(updatedSomeHousehold, updatedAnotherHousehold));
	}

	private Predicate<Household> onlySecondHousehold() {
		return h -> 2 == h.id();
	}

	private Household newHousehold(int id, double weight) {
		return new Household(id, weight, householdAttributes(), personAttributes());
	}

	private Map<String, Integer> personAttributes() {
		return emptyMap();
	}

	private Map<String, Integer> householdAttributes() {
		return emptyMap();
	}

	private BaseConstraint newConstraint() {
		return newConstraint(h -> true);
	}

	private BaseConstraint newConstraint(Predicate<Household> filter) {
		return new BaseConstraint(requestedWeight) {

			@Override
			protected double totalWeight(Household household) {
				return household.weight();
			}

			@Override
			protected boolean matches(Household household) {
				return filter.test(household);
			}
		};
	}
}
