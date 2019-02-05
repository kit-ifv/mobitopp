package edu.kit.ifv.mobitopp.data.demand;

import java.util.NoSuchElementException;

public class ContinuousDistribution
    extends AbstractDemandModelDistribution<ContinuousDistributionItem>
    implements ContinuousDistributionIfc {

  private static final long serialVersionUID = 1L;

  public ContinuousDistribution() {
    super();
  }

  @Override
  public boolean hasItem(int value) {
    return items().anyMatch(item -> item.matches(value));
  }

  @Override
  public ContinuousDistributionItem getItem(int value) {
    if (0 > value) {
      throw new IllegalArgumentException("Value must be above 0, but was: " + value);
    }

    return getItemsInternal()
        .stream()
        .sequential()
        .filter(item -> item.matches(value))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("No element found for value: " + value));
  }

  public int getTotalAmount() {
    return getItemsInternal().stream().mapToInt(ContinuousDistributionItem::amount).sum();
  }

  @Override
  public ContinuousDistributionIfc createEmpty() {
    ContinuousDistribution empty = new ContinuousDistribution();
    items().map(ContinuousDistributionItem::createEmpty).forEach(empty::addItem);
    return empty;
  }

}
