package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import nl.jqno.equalsverifier.EqualsVerifier;

public class WeightedHouseholdTest {

	private HouseholdOfPanelDataId id;
	private double weight;
	private int householdType1;
	private int personType1;
	private WeightedHousehold household;
	private HashMap<String, Integer> attributes;

	@Before
	public void initialise() {
		short year = 2000;
		id = new HouseholdOfPanelDataId(year, 1);
		weight = 1.0d;
		householdType1 = 1;
		personType1 = 1;
		attributes = new HashMap<>();
		attributes.put("Household:Type:1", householdType1);
		attributes.put("Person:Type:1", personType1);
		household = new WeightedHousehold(id, weight, attributes);
	}

	@Test
	public void createsHouseholdWithNewWeight() {
		double newWeight = 2.0d;
		WeightedHousehold newHousehold = household.newWeight(newWeight);

		assertThat(newHousehold, is(equalTo(new WeightedHousehold(id, newWeight, attributes))));
	}

	@Test
	public void getHouseholdAttributes() {
		int attribute = household.attribute("Household:Type:1");

		assertThat(attribute, is(equalTo(householdType1)));
	}

	@Test
	public void getPersonAttributes() {
		int attribute = household.attribute("Person:Type:1");

		assertThat(attribute, is(equalTo(personType1)));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(WeightedHousehold.class).usingGetClass().verify();
	}
}
