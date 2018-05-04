package edu.kit.ifv.mobitopp.data.demand;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.Employment;

public class EmploymentDistributionItemTest {

	@Test
	public void createsEmpty() {
		Employment employment = Employment.FULLTIME;
		int amount = 2;
		EmploymentDistributionItem item = new EmploymentDistributionItem(employment, amount);

		EmploymentDistributionItem emptyItem = new EmploymentDistributionItem(employment, 0);
		assertThat(item.createEmpty(), is(equalTo(emptyItem)));
	}

	@Test
	public void increasesAmount() {
		Employment employment = Employment.FULLTIME;
		int amount = 2;
		EmploymentDistributionItem item = new EmploymentDistributionItem(employment, amount);

		item.increment();

		assertThat(item.amount(), is(equalTo(amount + 1)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsOnMissingEmployment() {
		new EmploymentDistributionItem(null, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsOnAmount() {
		int tooLowAmount = -1;
		new EmploymentDistributionItem(Employment.FULLTIME, tooLowAmount);
	}
}
