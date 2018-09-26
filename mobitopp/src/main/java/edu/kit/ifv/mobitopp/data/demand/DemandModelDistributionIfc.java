package edu.kit.ifv.mobitopp.data.demand;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.stream.Stream;

/**
 * Interface for a demand model distributions.
 *
 * @version $Id: DemandModelDistributionIfcG.java,v 1.1.1.2 2010-09-22 13:44:29 mallig Exp $
 */

public interface DemandModelDistributionIfc<T extends DemandModelDistributionItemIfc>
		extends Serializable {

	/**
	 * Adds the specified item to the demand model distribution.
	 * 
	 * @param item
	 *          the demand model distribution item to be added.
	 */

	void addItem(T item);

	/**
	 * Returns the items in the demand model distribution
	 * 
	 * @return the items in the demand model distribution.
	 */

	SortedSet<T> getItems();
	
	Stream<T> items();

	/**
	 * Returns the total amount of persons in the demand model distribution.
	 * 
	 * @return the total amount of persons in the demand model distribution.
	 */

	public int getTotalAmount();

}
