package edu.kit.ifv.mobitopp.data.demand;

import java.io.Serializable;
import java.util.SortedSet;

/**
 * Interface for a demand model distributions.
 *
 * @version $Id: DemandModelDistributionIfcG.java,v 1.1.1.2 2010-09-22 13:44:29 mallig Exp $
 */

public interface DemandModelDistributionIfc<T extends DemandModelDistributionItemIfc> extends Serializable
{
  /**
   * Checks if the specified item exists in the demand model distribution.
   * 
   * @param item the demand model distribution item to be checked.
   * @return <code>true</code> if the item exists in the demand model distribution,
   *         <code>false</code> otherwise.
   */

  boolean existsItem(T item);

  /**
   * Adds the specified item to the demand model distribution.
   * 
   * @param item the demand model distribution item to be added.
   */

  void addItem(T item);

  /**
   * Removes the specified item to the demand model distribution.
   * 
   * @param item the demand model distribution item to be removed.
   */

  void removeItem(T item);

  /**
   * Returns the items in the demand model distribution
   * 
   * @return the items in the demand model distribution.
   */

  /**
   * Checks if the specified item exists in the demand model distribution.
   * 
   * @param item the demand model distribution item to be checked.
   * @return <code>true</code> if the item exists in the demand model distribution,
   *         <code>false</code> otherwise.
   */

  int getIndexOfItem(T item);


  SortedSet<T> getItems();

  /**
   * Returns the total amount of persons in the demand model distribution.
   * 
   * @return the total amount of persons in the demand model distribution.
   */

  public int getTotalAmount();
  
  /**
   * Returns the amount per item in the demand model distribution.
   * 
   * @return the amount per item in the demand model distribution.
   */

  public int[] getAmountPerItem();
}
