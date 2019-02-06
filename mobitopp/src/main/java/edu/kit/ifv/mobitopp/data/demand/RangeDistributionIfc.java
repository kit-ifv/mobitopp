package edu.kit.ifv.mobitopp.data.demand;

/**
 * Interface for a distributions with range based items. Each item has a lower and upper bound.
 * 
 */

public interface RangeDistributionIfc
    extends DemandModelDistributionIfc<RangeDistributionItem>
{
  /**
   * Returns the item for the specified value.
   * 
   * @param value the value for which the item should be returned.
   * @return the found distribution item for the value
   */

  RangeDistributionItem getItem(int value);

  boolean hasItem(int value);

  /**
   * Create a distribution containing all items with a value of 0.
   * 
   * @return a distribution containing all item with a value of 0
   */
  RangeDistributionIfc createEmpty();
}
