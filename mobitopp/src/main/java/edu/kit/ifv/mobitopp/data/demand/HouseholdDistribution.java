package edu.kit.ifv.mobitopp.data.demand;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

		throw new NoSuchElementException();
	}

  /**
   * Checkes whether or not there exist any 4+ housholds in this distribution.
   * 
   * @return <code>true</code> if there exist any 4+ housholds in this distribution,
   *         <code>false</code> otherwise.
   */
  
  public boolean existsPersonsInFourPlusHouseholds()
  {
    boolean flag = false;

    Iterator<HouseholdDistributionItem> iterator = getItemsInternal().iterator();
    while (iterator.hasNext())
    {
      HouseholdDistributionItem item = iterator.next();
           
      switch (item.type())
      {
        case 1:
        case 2:
        case 3:
          break;
        default:
        {
        	throw new IllegalStateException("Person does not exist in 4+ households for type: " + item.type());
        }
      }
    }

    return flag;
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

	public int[] getAmountPerItem() {
		int[] items = new int[getItemsInternal().size()];

		Iterator<HouseholdDistributionItem> iterator = getItemsInternal().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			HouseholdDistributionItem item = iterator.next();

			items[i] = item.amount();
		}

		return items;
	}

	public int getIndexOfItem(HouseholdDistributionItem searchedItem) {
		Iterator<HouseholdDistributionItem> iterator = getItemsInternal().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			HouseholdDistributionItem item = iterator.next();

			if (item.type() == searchedItem.type()) {
				return i;
			}
		}

		return -1;
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
