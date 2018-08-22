package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class IpuTest {

	private List<Household> households;
	private Household household2;
	private Household household1;
	private Household household3;
	private Household household4;
	private Household household5;
	private Household household6;
	private Household household7;
	private Household household8;
	private Ipu ipu;

	@Before
	public void initialise() {
		int hhid = 1;
		double baseWeight = 1.0d;
		household1 = newHousehold(hhid++, baseWeight, 1, 0, 1, 1, 1);
		household2 = newHousehold(hhid++, baseWeight, 1, 0, 1, 0, 1);
		household3 = newHousehold(hhid++, baseWeight, 1, 0, 2, 1, 0);
		household4 = newHousehold(hhid++, baseWeight, 0, 1, 1, 0, 2);
		household5 = newHousehold(hhid++, baseWeight, 0, 1, 0, 2, 1);
		household6 = newHousehold(hhid++, baseWeight, 0, 1, 1, 1, 0);
		household7 = newHousehold(hhid++, baseWeight, 0, 1, 2, 1, 2);
		household8 = newHousehold(hhid++, baseWeight, 0, 1, 1, 1, 0);
		households = asList(household1, household2, household3, household4, household5, household6,
				household7, household8);
		List<Constraint> constraints = new ArrayList<>();
		constraints.add(new HouseholdConstraint(24, h -> h.householdAttribute("Household:Type:1")));
		constraints.add(new HouseholdConstraint(60, h -> h.householdAttribute("Household:Type:2")));
		constraints.add(new PersonConstraint(46, h -> h.personAttribute("Person:Type:1")));
		constraints.add(new PersonConstraint(25, h -> h.personAttribute("Person:Type:2")));
		constraints.add(new PersonConstraint(10, h -> h.personAttribute("Person:Type:3")));
		ipu = new Ipu(constraints);
	}

	private Household newHousehold(
			int i, double baseWeight, int householdType1, int householdType2, int personType1,
			int personType2, int personType3) {
		Map<String, Integer> attributes = new HashMap<>();
		attributes.put("Household:Type:1", householdType1);
		attributes.put("Household:Type:2", householdType2);
		Map<String, Integer> personAttributes = new HashMap<>();
		personAttributes.put("Person:Type:1", personType1);
		personAttributes.put("Person:Type:2", personType2);
		personAttributes.put("Person:Type:3", personType3);
		return new Household(i, baseWeight, attributes, personAttributes);
	}

	@Test
	public void adjustHouseholdWeightsWithSize() {
		List<Household> adjusted = ipu.adjustHouseholdWeights(households);

		assertThat(adjusted, hasSize(households.size()));
		List<Household> updatedHouseholds = updatedHouseholds();
		assertThat(adjusted, containsInAnyOrder(updatedHouseholds.toArray()));
		updatedHouseholds().forEach(System.out::println);
	}

	private List<Household> updatedHouseholds() {
		double householdType1Weight = 24.0d / 3.0d;
		double householdType2Weight = 60.0d / 5.0d;
		double factorPersonType1Update = 46.0 / 92.0d;
		double factorPersonType2Update = 25.0 / 50.0d;
		double factorPersonType3Update = 10.0 / 30.0d;
		Household updatedHousehold1 = household1
				.newWeight(householdType1Weight * factorPersonType1Update * factorPersonType2Update
						* factorPersonType3Update);
		Household updatedHousehold2 = household2
				.newWeight(householdType1Weight * factorPersonType1Update * factorPersonType3Update);
		Household updatedHousehold3 = household3
				.newWeight(householdType1Weight * factorPersonType1Update * factorPersonType2Update);
		Household updatedHousehold4 = household4
				.newWeight(householdType2Weight * factorPersonType1Update * factorPersonType3Update);
		Household updatedHousehold5 = household5
				.newWeight(householdType2Weight * factorPersonType2Update * factorPersonType3Update);
		Household updatedHousehold6 = household6
				.newWeight(householdType2Weight * factorPersonType1Update * factorPersonType2Update);
		Household updatedHousehold7 = household7
				.newWeight(householdType2Weight * factorPersonType1Update * factorPersonType2Update
						* factorPersonType3Update);
		Household updatedHousehold8 = household8
				.newWeight(householdType2Weight * factorPersonType1Update * factorPersonType2Update);
		return asList(updatedHousehold1, updatedHousehold2, updatedHousehold3, updatedHousehold4,
				updatedHousehold5, updatedHousehold6, updatedHousehold7, updatedHousehold8);
	}

}
