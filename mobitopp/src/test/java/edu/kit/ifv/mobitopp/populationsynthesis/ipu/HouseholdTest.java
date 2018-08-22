package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class HouseholdTest {

	private int id;
	private double weight;
	private int householdType1;
	private int householdType2;
	private int personType1;
	private int personType2;
	private int personType3;
	private Household household;
	private HashMap<String, Integer> householdAttributes;
	private Map<String, Integer> personAttributes;

	@Before
	public void initialise() {
		id = 1;
		weight = 1.0d;
		householdType1 = 1;
		householdType2 = 0;
		personType1 = 1;
		personType2 = 1;
		personType3 = 1;
		householdAttributes = new HashMap<>();
		householdAttributes.put("Household:Type:1", householdType1);
		householdAttributes.put("Household:Type:2", householdType2);
		personAttributes = new HashMap<>();
		personAttributes.put("Person:Type:1", personType1);
		personAttributes.put("Person:Type:2", personType2);
		personAttributes.put("Person:Type:3", personType3);
		household = new Household(id, weight, householdAttributes, personAttributes);
	}

	@Test
	public void createsHouseholdWithNewWeight() {

		double newWeight = 2.0d;
		Household newHousehold = household.newWeight(newWeight);

		assertThat(newHousehold,
				is(equalTo(new Household(id, newWeight, householdAttributes, personAttributes))));
	}

	@Test
	public void getHouseholdAttributes() {
		int attribute = household.householdAttribute("Household:Type:1");

		assertThat(attribute, is(equalTo(householdType1)));
	}

	@Test
	public void getPersonAttributes() {
		int attribute = household.personAttribute("Person:Type:1");

		assertThat(attribute, is(equalTo(personType1)));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(Household.class).usingGetClass().verify();
	}
}
