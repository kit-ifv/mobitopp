package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;

public class HouseholdDistributionBuilderTest {

	private StructuralData demographyData;
	private HouseholdDistributionBuilder builder;

	@Before
	public void initialise() {
		demographyData = Example.demographyData();
		builder = new HouseholdDistributionBuilder(demographyData);
	}

	@Test
	public void build() {
    String zoneId = "1";
		HouseholdDistribution distribution = builder.build(zoneId);
	
		assertThat(distribution, is(equalTo(expectedHouseholds())));
	}

	private HouseholdDistribution expectedHouseholds() {
		HouseholdDistribution distribution = new HouseholdDistribution();
		distribution.addItem(new HouseholdDistributionItem(1, 161));
		distribution.addItem(new HouseholdDistributionItem(2, 508));
		distribution.addItem(new HouseholdDistributionItem(3, 29));
		distribution.addItem(new HouseholdDistributionItem(4, 16));
		distribution.addItem(new HouseholdDistributionItem(5, 402));
		distribution.addItem(new HouseholdDistributionItem(6, 287));
		distribution.addItem(new HouseholdDistributionItem(7, 4));
		distribution.addItem(new HouseholdDistributionItem(8, 108));
		distribution.addItem(new HouseholdDistributionItem(9, 169));
		distribution.addItem(new HouseholdDistributionItem(10, 6));
		distribution.addItem(new HouseholdDistributionItem(11, 122));
		distribution.addItem(new HouseholdDistributionItem(12, 233));
		return distribution;
	}
}
