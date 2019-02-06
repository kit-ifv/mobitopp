package edu.kit.ifv.mobitopp.data.demand;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RangeDistributionItemTest {

	@Test
	public void createsEmpty() {
		int lowerBound = 1;
		int upperBound = 1;
		int amount = 2;
		RangeDistributionItem item = new RangeDistributionItem(lowerBound, upperBound, amount);

		RangeDistributionItem emptyItem = new RangeDistributionItem(lowerBound, upperBound, 0);
		assertThat(item.createEmpty(), is(equalTo(emptyItem)));
	}

	@Test
	public void increasesAmount() {
		int lowerBound = 1;
		int upperBound = 1;
		int amount = 2;
		RangeDistributionItem item = new RangeDistributionItem(lowerBound, upperBound, amount);

		item.increment();

		assertThat(item.amount(), is(equalTo(amount + 1)));
	}

	@Test
	public void matchesBounds() {
		int lowerBound = 1;
		int upperBound = 1;
		int amount = 2;
		RangeDistributionItem item = new RangeDistributionItem(lowerBound, upperBound, amount);

		assertTrue(item.matches(lowerBound));
		assertTrue(item.matches(upperBound));
	}
	
	@Test
	public void matchesOutOfBounds() {
		int lowerBound = 1;
		int upperBound = 1;
		int belowLowerBound = lowerBound - 1;
		int aboveUpperBound = upperBound + 1;
		int amount = 2;
		RangeDistributionItem item = new RangeDistributionItem(lowerBound, upperBound, amount);
		
		assertFalse(item.matches(belowLowerBound));
		assertFalse(item.matches(aboveUpperBound));
	}
	
	@Test
	public void matchesBetweenBounds() {
		int lowerBound = 1;
		int upperBound = 3;
		int betweenBound = 2;
		int amount = 2;
		RangeDistributionItem item = new RangeDistributionItem(lowerBound, upperBound, amount);
		
		assertTrue(item.matches(betweenBound));
	}
	
	@Test
	public void comparesBounds() {
		int lower = 1;
		int higher = 3;
		int amount = 2;
		RangeDistributionItem lowerItem = new RangeDistributionItem(lower, lower, amount);
		RangeDistributionItem higherItem = new RangeDistributionItem(higher, higher, amount);
		
		assertThat(lowerItem.compareTo(higherItem), is(lessThan(0)));
		assertThat(higherItem.compareTo(lowerItem), is(greaterThan(0)));
		assertThat(higherItem.compareTo(higherItem), is(equalTo(0)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsOnLowerBound() {
		new RangeDistributionItem(-1, 0, 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void failsOnUpperBound() {
		new RangeDistributionItem(0, -1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsOnAmount() {
		new RangeDistributionItem(0, 0, -1);
	}
}
