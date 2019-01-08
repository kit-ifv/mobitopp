package edu.kit.ifv.mobitopp.data.demand;

/**
 * Interface for a continuous distributions.
 *
 * @version $Id: AgeDistributionIfcG.java,v 1.1.1.2 2010-09-22 13:44:30 mallig Exp $
 */

public interface ContinuousDistributionIfc
    extends DemandModelDistributionIfc<ContinuousDistributionItem>
{
  /**
   * Returns the item for the specified value.
   * 
   * @param value the value for which the item should be returned.
   * @return the found distribution item for the value
   */

  ContinuousDistributionItem getItem(int value);
}
