package edu.kit.ifv.mobitopp.data.demand;

import java.util.NoSuchElementException;

public class RangeDistribution
    extends AbstractDemandModelDistribution<RangeDistributionItem>
    implements RangeDistributionIfc {

  private static final long serialVersionUID = 1L;

  public RangeDistribution() {
    super();
  }

  @Override
  public boolean hasItem(int value) {
    return items().anyMatch(item -> item.matches(value));
  }

  @Override
  public RangeDistributionItem getItem(int value) {
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
    return getItemsInternal().stream().mapToInt(RangeDistributionItem::amount).sum();
  }

  @Override
  public RangeDistributionIfc createEmpty() {
    RangeDistribution empty = new RangeDistribution();
    items().map(RangeDistributionItem::createEmpty).forEach(empty::addItem);
    return empty;
  }

  @Override
  public void increment(int value) {
    getItem(value).increment();
  }

}
