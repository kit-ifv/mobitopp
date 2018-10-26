package edu.kit.ifv.mobitopp.data.demand;

import java.util.Iterator;

public class HouseholdDistribution extends
		AbstractDemandModelDistribution<HouseholdDistributionItem> implements HouseholdDistributionIfc {

	private static final long serialVersionUID = 1L;

	public HouseholdDistribution() {
		super();
	}

  /**
   * Returns the item for the specified household type.
   * 
   * @param type the household type for which the item should be returned.
   * @return the found household distribution item
   */
	public HouseholdDistributionItem getItem(int type) {
		Iterator<HouseholdDistributionItem> iterator = getItemsInternal().iterator();
		while (iterator.hasNext()) {
			HouseholdDistributionItem anItem = iterator.next();

			if (anItem.type() == type) {
				return anItem;
			}
		}

		HouseholdDistributionItem anItem = new HouseholdDistributionItem(type, 0);
		getItemsInternal().add(anItem);
		return anItem;
	}

  public int getTotalAmount() {
		int sum = 0;

		Iterator<HouseholdDistributionItem> iterator = getItemsInternal().iterator();
		while (iterator.hasNext()) {
			HouseholdDistributionItem item = iterator.next();

			sum += item.amount();
		}

		return sum;
	}

	public static HouseholdDistribution createDefault() {
		HouseholdDistribution distribution = new HouseholdDistribution();

		distribution.addItem(new HouseholdDistributionItem(1, 0));
		distribution.addItem(new HouseholdDistributionItem(2, 0));
		distribution.addItem(new HouseholdDistributionItem(3, 0));
		distribution.addItem(new HouseholdDistributionItem(4, 0));

		return distribution;
	}   
}
