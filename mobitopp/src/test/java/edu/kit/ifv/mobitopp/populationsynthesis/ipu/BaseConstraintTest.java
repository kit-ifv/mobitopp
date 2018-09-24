package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Test;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class BaseConstraintTest {

	private static final double margin = 1e-6;
	private static final HouseholdOfPanelDataId someId = new HouseholdOfPanelDataId(2000, 1);
	private static final HouseholdOfPanelDataId anotherId = new HouseholdOfPanelDataId(2000, 2);
	private static final double requestedWeight = 6.0d;

	@Test
	public void updateWeightsOnAllHousehold() {
		Household someHousehold = newHousehold(someId, 1.0d);
		Household anotherHousehold = newHousehold(anotherId, 2.0d);
		List<Household> households = asList(someHousehold, anotherHousehold);
		BaseConstraint constraint = newConstraint();

		List<Household> updatedHouseholds = constraint.scaleWeightsOf(households);

		Household updatedSomeHousehold = newHousehold(someId, 2.0d);
		Household updatedAnotherHousehold = newHousehold(anotherId, 4.0d);
		assertThat(updatedHouseholds,
				containsInAnyOrder(updatedSomeHousehold, updatedAnotherHousehold));
	}

	@Test
	public void updateWeightOnSingleHousehold() {
		Household someHousehold = newHousehold(someId, 1.0d);
		Household anotherHousehold = newHousehold(anotherId, 2.0d);
		List<Household> households = asList(someHousehold, anotherHousehold);
		BaseConstraint constraint = newConstraint(onlyAnotherHousehold());

		List<Household> updatedHouseholds = constraint.scaleWeightsOf(households);

		Household updatedSomeHousehold = newHousehold(someId, 1.0d);
		Household updatedAnotherHousehold = newHousehold(anotherId, 6.0d);
		assertThat(updatedHouseholds,
				containsInAnyOrder(updatedSomeHousehold, updatedAnotherHousehold));
	}
	
	@Test
	public void calculatesGoodnessOfFit() {
		Household someHousehold = newHousehold(someId, 1.0d);
		Household anotherHousehold = newHousehold(anotherId, 2.0d);
		List<Household> households = asList(someHousehold, anotherHousehold);
		BaseConstraint constraint = newConstraint();
		
		double goodnessOfFit = constraint.calculateGoodnessOfFitFor(households);
		
		assertThat(goodnessOfFit, is(closeTo(0.5d, margin)));
	}
	
	@Test
	public void calculatesAnotherGoodnessOfFit() {
		Household someHousehold = newHousehold(someId, 2.0d);
		Household anotherHousehold = newHousehold(anotherId, 4.0d);
		List<Household> households = asList(someHousehold, anotherHousehold);
		BaseConstraint constraint = newConstraint();
		
		double goodnessOfFit = constraint.calculateGoodnessOfFitFor(households);
		
		assertThat(goodnessOfFit, is(closeTo(0.0d, margin)));
	}

	private Predicate<Household> onlyAnotherHousehold() {
		return h -> anotherId == h.id();
	}

	private Household newHousehold(HouseholdOfPanelDataId id, double weight) {
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
