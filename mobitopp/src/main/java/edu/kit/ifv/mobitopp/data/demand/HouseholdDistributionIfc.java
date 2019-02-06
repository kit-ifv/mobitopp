package edu.kit.ifv.mobitopp.data.demand;

/**
 * Interface for a age distributions.
 *
 * @version $Id: HouseholdDistributionIfcG.java,v 1.1.1.2 2010-09-22 13:44:30 mallig Exp $
 */

public interface HouseholdDistributionIfc
    extends DemandModelDistributionIfc<RangeDistributionItem>
{
  /**
   * Returns the item for the specified age.
   * 
   * @param age_ the age for which the item should be returned.
   * @return the found age distribution item
   */

  RangeDistributionItem getItem(int age_);
}
