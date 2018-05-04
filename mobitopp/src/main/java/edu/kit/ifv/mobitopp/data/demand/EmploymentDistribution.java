package edu.kit.ifv.mobitopp.data.demand;

import java.util.Iterator;

import edu.kit.ifv.mobitopp.simulation.Employment;

public class EmploymentDistribution
		extends AbstractDemandModelDistribution<EmploymentDistributionItem> {

	private static final long serialVersionUID = 1L;

	public EmploymentDistribution() {
		super();
	}

  /**
   * Returns the item for the specified zone employment type.
   * 
   * @param type the zone employment type for which the item should be returned.
   * @return the found employment distribution item
   */
	public EmploymentDistributionItem getItem(Employment type) {
		Iterator<EmploymentDistributionItem> iterator = getItemsInternal().iterator();
		while (iterator.hasNext()) {
			EmploymentDistributionItem anItem = iterator.next();

			if (anItem.getTypeAsInt() == type.getTypeAsInt()) {
				return anItem;
			}
		}

		EmploymentDistributionItem anItem = new EmploymentDistributionItem(type, 0);
		getItemsInternal().add(anItem);
		return anItem;
	}

	public int getTotalAmount() {
		int sum = 0;

		Iterator<EmploymentDistributionItem> iterator = getItemsInternal().iterator();
		while (iterator.hasNext()) {
			EmploymentDistributionItem item = iterator.next();

			sum += item.amount();
		}

		return sum;
	}

	public int[] getAmountPerItem() {
		int[] items = new int[getItemsInternal().size()];

		Iterator<EmploymentDistributionItem> iterator = getItemsInternal().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			EmploymentDistributionItem item = iterator.next();

			items[i] = item.amount();
		}

		return items;
	}

	public int getIndexOfItem(EmploymentDistributionItem searchedItem) {
		Iterator<EmploymentDistributionItem> iterator = getItemsInternal().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			EmploymentDistributionItem item = iterator.next();

			if (item.getTypeAsInt() == searchedItem.getTypeAsInt()) {
				return i;
			}
		}

		return -1;
	}
  
	public static EmploymentDistribution createDefault() {
		EmploymentDistribution distribution = new EmploymentDistribution();

		distribution.addItem(new EmploymentDistributionItem(Employment.NONE, 0));
		distribution.addItem(new EmploymentDistributionItem(Employment.PARTTIME, 0));
		distribution.addItem(new EmploymentDistributionItem(Employment.FULLTIME, 0));

		return distribution;
	}
}
