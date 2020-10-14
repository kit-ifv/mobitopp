package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class WeightedHouseholdTest {

	private HouseholdOfPanelDataId id;
	private double weight;
	private int householdType1;
	private int personType1;
	private WeightedHousehold household;
	private HashMap<String, Integer> attributes;
	private RegionalContext context;
  private HouseholdOfPanelData panelHousehold;

	@BeforeEach
	public void initialise() {
		short year = 2000;
		id = new HouseholdOfPanelDataId(year, 1);
		panelHousehold = new HouseholdOfPanelDataBuilder().withId(id).build();
		weight = 1.0d;
		householdType1 = 1;
		personType1 = 1;
		attributes = new HashMap<>();
		attributes.put("Household:Type:1", householdType1);
		attributes.put("Person:Type:1", personType1);
		context = new DefaultRegionalContext(RegionalLevel.community, "1");
		household = new WeightedHousehold(id, weight, attributes, context, panelHousehold);
	}

	@Test
	public void createsHouseholdWithNewWeight() {
		double newWeight = 2.0d;
		household.setWeight(newWeight);

		assertThat(household.weight()).isEqualTo(newWeight);
	}

	@Test
	public void getHouseholdAttributes() {
		int attribute = household.attribute("Household:Type:1");

		assertThat(attribute).isEqualTo(householdType1);
	}

	@Test
	public void getPersonAttributes() {
		int attribute = household.attribute("Person:Type:1");

		assertThat(attribute).isEqualTo(personType1);
	}
	
	@Test
  public void getMissingAttribute() {
    int attribute = household.attribute("Missing:Attribute:1");

    assertThat(attribute).isEqualTo(0);    
  }
	
	@Test
	void copiesHousehold() throws Exception {
		WeightedHousehold copy = new WeightedHousehold(household);
		
		assertThat(copy).isEqualTo(household);
		assertThat(copy).isNotSameAs(household);
	}

}
