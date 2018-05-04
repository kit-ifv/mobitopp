package edu.kit.ifv.mobitopp.data.demand;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem.Type;

public class AgeDistributionItemTest {

	@Test
	public void createsEmpty() {
		Type sign = Type.UNTIL;
		int age = 1;
		int amount = 2;
		AgeDistributionItem item = new AgeDistributionItem(sign, age, amount);

		AgeDistributionItem emptyItem = new AgeDistributionItem(sign, age, 0);
		assertThat(item.createEmpty(), is(equalTo(emptyItem)));
	}
	
	@Test
	public void increasesAmount() {
		Type sign = Type.UNTIL;
		int age = 1;
		int amount = 2;
		AgeDistributionItem item = new AgeDistributionItem(sign, age, amount);

		item.increment();
		
		assertThat(item.amount(), is(equalTo(amount + 1)));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failsOnMissingSign() {
		new AgeDistributionItem(null, 0, 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failsOnAge() {
		new AgeDistributionItem(Type.UNTIL, -1, 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failsOnAmount() {
		new AgeDistributionItem(Type.UNTIL, 0, -1);
	}
}
