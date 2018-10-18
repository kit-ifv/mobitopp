package edu.kit.ifv.mobitopp.data.demand;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractAgeDistribution
		extends AbstractDemandModelDistribution<AgeDistributionItem> implements AgeDistributionIfc {

	private static final long serialVersionUID = 1L;

	protected AbstractAgeDistribution() {
		super();
	}
	
	public boolean hasItem(int age) {
		return items().anyMatch(item -> item.matches(age));
	}

	public AgeDistributionItem getItem(int age) {
		if (0 > age) {
			throw new IllegalArgumentException("Age must be above 0, but was: " + age);
		}

		return getItemsInternal()
				.stream()
				.sequential()
				.filter(item -> item.matches(age))
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException("No element found for age: " + age));
	}

	public int getTotalAmount() {
		int sum = 0;

		Iterator<AgeDistributionItem> iterator = getItemsInternal().iterator();
		while (iterator.hasNext()) {
			AgeDistributionItem item = iterator.next();

			sum += item.amount();
		}

		return sum;
	}
  
}
