package edu.kit.ifv.mobitopp.data.demand;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class HouseholdDistributionItemTest {

	@Test
	public void createsEmptyItem() {
		int type = 1;
		int amount = 2;
		HouseholdDistributionItem item = new HouseholdDistributionItem(type, amount);
		
		int emptyAmount = 0;
		HouseholdDistributionItem emptyItem = new HouseholdDistributionItem(type, emptyAmount);
		assertThat(item.createEmpty(), is(equalTo(emptyItem)));
	}
	
	@Test
	public void increasesAmount() {
		int type = 1;
		int amount = 2;
		HouseholdDistributionItem item = new HouseholdDistributionItem(type, amount);
		
		item.increment();
		
		assertThat(item.amount(), is(equalTo(amount + 1)));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failsOnType() {
		int tooLowType = -1;
		new HouseholdDistributionItem(tooLowType, 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failsOnAmount() {
		int tooLowAmount = -1;
		new HouseholdDistributionItem(0, tooLowAmount);
	}
}
