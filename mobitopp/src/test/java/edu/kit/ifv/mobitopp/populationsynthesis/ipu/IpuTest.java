package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class IpuTest {

	private List<Household> households;
	private Household household2;
	private Household household1;
	private Ipu ipu;
	private Constraint someConstraint;
	private Constraint anotherConstraint;
	private List<Household> afterSomeConstraint;
	private List<Household> afterAnotherConstraint;

	@Before
	public void initialise() {
		createHouseholds();
		createConstraints();
		ipu = new Ipu(asList(someConstraint, anotherConstraint));
	}

	private void createHouseholds() {
		int hhid = 1;
		double baseWeight = 1.0d;
		household1 = newHousehold(hhid++, baseWeight, 1, 1);
		household2 = newHousehold(hhid++, baseWeight, 1, 1);
		households = asList(household1, household2);
		afterSomeConstraint = asList(household1.newWeight(2.0d), household2.newWeight(3.0d));
		afterAnotherConstraint = asList(household1.newWeight(3.0d), household2.newWeight(4.0d));
	}

	private void createConstraints() {
		someConstraint = mock(Constraint.class);
		anotherConstraint = mock(Constraint.class);
		when(someConstraint.update(households)).thenReturn(afterSomeConstraint);
		when(anotherConstraint.update(afterSomeConstraint)).thenReturn(afterAnotherConstraint);
	}

	private Household newHousehold(int i, double baseWeight, int householdType, int personType) {
		Map<String, Integer> householdAttributes = singletonMap("some attribute", householdType);
		Map<String, Integer> personAttributes = singletonMap("another attribute", personType);
		return new Household(i, baseWeight, householdAttributes, personAttributes);
	}

	@Test
	public void adjustHouseholdWeightsWithSize() {
		List<Household> adjusted = ipu.adjustHouseholdWeights(households);

		verify(someConstraint).update(households);
		verify(anotherConstraint).update(afterSomeConstraint);
		assertThat(adjusted, hasSize(households.size()));
		assertThat(adjusted, is(equalTo(afterAnotherConstraint)));
	}

}
