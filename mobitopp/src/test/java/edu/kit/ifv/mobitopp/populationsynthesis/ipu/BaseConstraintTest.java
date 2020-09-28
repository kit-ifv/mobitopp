package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.function.Predicate;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import nl.jqno.equalsverifier.EqualsVerifier;

public class BaseConstraintTest {

	private static final Offset<Double> margin = Offset.offset(1e-6d);
	private static final short year = 2000;
	private static final HouseholdOfPanelDataId someId = new HouseholdOfPanelDataId(year, 1);
	private static final HouseholdOfPanelDataId anotherId = new HouseholdOfPanelDataId(year, 2);
	private static final double requestedWeight = 6.0d;

	@Test
	public void updateWeightsOnAllHousehold() {
		WeightedHousehold someHousehold = newHousehold(someId, 1.0d);
		WeightedHousehold anotherHousehold = newHousehold(anotherId, 2.0d);
		WeightedHouseholds households = new WeightedHouseholds(asList(someHousehold, anotherHousehold));
		BaseConstraint constraint = newConstraint();

		WeightedHouseholds updatedHouseholds = constraint.scaleWeightsOf(households);

		WeightedHousehold updatedSomeHousehold = newHousehold(someId, 2.0d);
		WeightedHousehold updatedAnotherHousehold = newHousehold(anotherId, 4.0d);
		assertThat(updatedHouseholds.toList())
				.containsExactly(updatedSomeHousehold, updatedAnotherHousehold);
	}

	@Test
	public void updateWeightOnSingleHousehold() {
		WeightedHousehold someHousehold = newHousehold(someId, 1.0d);
		WeightedHousehold anotherHousehold = newHousehold(anotherId, 2.0d);
		WeightedHouseholds households = new WeightedHouseholds(asList(anotherHousehold, someHousehold));
		BaseConstraint constraint = newConstraint(onlyAnotherHousehold());

		WeightedHouseholds updatedHouseholds = constraint.scaleWeightsOf(households);

		WeightedHousehold updatedSomeHousehold = newHousehold(someId, 1.0d);
		WeightedHousehold updatedAnotherHousehold = newHousehold(anotherId, 6.0d);
		assertThat(updatedHouseholds.toList())
				.containsExactly(updatedAnotherHousehold, updatedSomeHousehold);
	}

	@Test
	public void calculatesGoodnessOfFit() {
		WeightedHousehold someHousehold = newHousehold(someId, 1.0d);
		WeightedHousehold anotherHousehold = newHousehold(anotherId, 2.0d);
		WeightedHouseholds households = new WeightedHouseholds(asList(someHousehold, anotherHousehold));
		BaseConstraint constraint = newConstraint();

		double goodnessOfFit = constraint.calculateGoodnessOfFitFor(households);

		assertThat(goodnessOfFit).isCloseTo(0.5d, margin);
	}

	@Test
	public void calculatesAnotherGoodnessOfFit() {
		WeightedHousehold someHousehold = newHousehold(someId, 2.0d);
		WeightedHousehold anotherHousehold = newHousehold(anotherId, 4.0d);
		WeightedHouseholds households = new WeightedHouseholds(asList(someHousehold, anotherHousehold));
		BaseConstraint constraint = newConstraint();

		double goodnessOfFit = constraint.calculateGoodnessOfFitFor(households);

		assertThat(goodnessOfFit).isCloseTo(0.0d, margin);
	}

	@Test
	public void requestsWeightToBeGreaterThanZero() {
		double weight = 0.0d;
		BaseConstraint constraint = newBaseConstraint(weight);

		BaseConstraint greaterZero = newBaseConstraint(BaseConstraint.greaterZero);
		assertThat(constraint).isEqualTo(greaterZero);
	}

	private BaseConstraint newBaseConstraint(double weight) {
		return new BaseConstraint(weight) {

			@Override
			protected double totalWeight(WeightedHousehold household) {
				return 0;
			}

			@Override
			protected boolean matches(WeightedHousehold household) {
				return false;
			}
		};
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(BaseConstraint.class).usingGetClass().verify();
	}

	private Predicate<WeightedHousehold> onlyAnotherHousehold() {
		return h -> anotherId == h.id();
	}

	private WeightedHousehold newHousehold(HouseholdOfPanelDataId id, double weight) {
		return new WeightedHousehold(id, weight, attributes(),
				new DefaultRegionalContext(RegionalLevel.community, "1"));
	}

	private Map<String, Integer> attributes() {
		return emptyMap();
	}

	private BaseConstraint newConstraint() {
		return newConstraint(h -> true);
	}

	private BaseConstraint newConstraint(Predicate<WeightedHousehold> filter) {
		return new BaseConstraint(requestedWeight) {

			@Override
			protected double totalWeight(WeightedHousehold household) {
				return household.weight();
			}

			@Override
			protected boolean matches(WeightedHousehold household) {
				return filter.test(household);
			}
		};
	}
}
