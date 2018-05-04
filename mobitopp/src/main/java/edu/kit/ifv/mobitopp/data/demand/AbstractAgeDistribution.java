package edu.kit.ifv.mobitopp.data.demand;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem.Type;

public abstract class AbstractAgeDistribution
		extends AbstractDemandModelDistribution<AgeDistributionItem> implements AgeDistributionIfc {

	private static final long serialVersionUID = 1L;

	protected AbstractAgeDistribution() {
		super();
	}

	public AgeDistributionItem getItem(int age) {
		if (0 > age) {
			throw new IllegalArgumentException("Age must be above 0, but was: " + age);
		}

		Iterator<AgeDistributionItem> iterator = getItemsInternal().iterator();
		while (iterator.hasNext()) {
			AgeDistributionItem anItem = iterator.next();

			if ((anItem.sign() == Type.UNTIL) && (age <= anItem.age())) {
				return anItem;
			}

			if ((anItem.sign() == Type.OVER) && (age >= anItem.age())) {
				return anItem;
			}
		}

		throw new NoSuchElementException();
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

	public int[] getAmountPerItem() {
		int[] items = new int[getItemsInternal().size()];

		Iterator<AgeDistributionItem> iterator = getItemsInternal().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			AgeDistributionItem item = iterator.next();

			items[i] = item.amount();
		}

		return items;
	}

	public int getIndexOfItem(AgeDistributionItem searchedItem) {
		Iterator<AgeDistributionItem> iterator = getItemsInternal().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			AgeDistributionItem item = iterator.next();

			if ((item.age() == searchedItem.age()) && (item.sign() == searchedItem.sign())) {
				return i;
			}
		}

		return -1;
	}
  
}
