package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import nl.jqno.equalsverifier.EqualsVerifier;

public class IpuIterationTest {

	private static final Offset<Double> margin = offset(1e-6);
	private static final RegionalContext context = new DefaultRegionalContext(RegionalLevel.community,
			"1");
	private WeightedHouseholds households;
	private WeightedHousehold household1;
	private WeightedHousehold household2;
	private Iteration ipu;
	private Constraint someConstraint;
	private Constraint anotherConstraint;
	private WeightedHouseholds afterSomeConstraint;
	private WeightedHouseholds afterAnotherConstraint;

	@Before
	public void initialise() {
		createHouseholds();
		createConstraints();
		ipu = new IpuIteration(asList(someConstraint, anotherConstraint));
	}

	private void createHouseholds() {
		int hhid = 1;
		double baseWeight = 1.0d;
		household1 = newHousehold(newPanelHousehold(hhid++), baseWeight, 1, 1);
		household2 = newHousehold(newPanelHousehold(hhid++), baseWeight, 1, 1);
		households = new WeightedHouseholds(asList(household1, household2));
		afterSomeConstraint = new WeightedHouseholds(
				asList(newWeight(household1, 2.0d), newWeight(household2, 3.0d)));
		afterAnotherConstraint = new WeightedHouseholds(
				asList(newWeight(household1, 3.0d), newWeight(household2, 4.0d)));
	}

	private WeightedHousehold newWeight(WeightedHousehold household, double weight) {
		WeightedHousehold copy = new WeightedHousehold(household);
		copy.setWeight(weight);
		return copy;
	}

	private HouseholdOfPanelData newPanelHousehold(int id) {
		short year = 2000;
		HouseholdOfPanelDataId householdId = new HouseholdOfPanelDataId(year, id);
		return new HouseholdOfPanelDataBuilder().withId(householdId).build();
	}

	private void createConstraints() {
		someConstraint = mock(Constraint.class);
		anotherConstraint = mock(Constraint.class);
		when(someConstraint.scaleWeightsOf(households)).thenReturn(afterSomeConstraint);
		when(anotherConstraint.scaleWeightsOf(afterSomeConstraint)).thenReturn(afterAnotherConstraint);
	}

	private WeightedHousehold newHousehold(
			HouseholdOfPanelData panelHousehold, double baseWeight, int householdType, int personType) {
		Map<String, Integer> attributes = new HashMap<>();
		attributes.put("some attribute", householdType);
		attributes.put("another attribute", personType);
		return new WeightedHousehold(panelHousehold.getId(), baseWeight, attributes, context, panelHousehold);
	}

	@Test
	public void adjustHouseholdWeightsWithSize() {
		WeightedHouseholds adjusted = ipu.adjustWeightsOf(households);

		assertThat(adjusted.size()).isEqualTo(households.size());
		assertThat(adjusted).isEqualTo(afterAnotherConstraint);

		verify(someConstraint).scaleWeightsOf(households);
		verify(anotherConstraint).scaleWeightsOf(afterSomeConstraint);
	}

	@Test
	public void returnsSameHouseholdsWithoutConstraints() {
		IpuIteration ipuIteration = new IpuIteration(Collections.emptyList());
		WeightedHouseholds adjusted = ipuIteration.adjustWeightsOf(households);

		assertThat(adjusted).isEqualTo(households);
	}

	@Test
	public void calculatesGoodnessOfFit() {
		double someGoodness = 0.75d;
		double anotherGoodness = 0.25d;
		when(someConstraint.calculateGoodnessOfFitFor(households)).thenReturn(someGoodness);
		when(anotherConstraint.calculateGoodnessOfFitFor(households)).thenReturn(anotherGoodness);
		double goodnessOfFit = ipu.calculateGoodnessOfFitFor(households);

		assertThat(goodnessOfFit).isCloseTo(0.5d, margin);

		verify(someConstraint).calculateGoodnessOfFitFor(households);
		verify(anotherConstraint).calculateGoodnessOfFitFor(households);
	}

	@Test
	public void calculatesNoGoodnessForEmptyConstraints() {
		IpuIteration ipuIteration = new IpuIteration(Collections.emptyList());
		
		double goodnessOfFit = ipuIteration.calculateGoodnessOfFitFor(households);
		
		assertThat(goodnessOfFit).isCloseTo(Double.MAX_VALUE, margin);
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(IpuIteration.class).usingGetClass().verify();
	}

}
