package edu.kit.ifv.mobitopp.data.demand;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RangeDistribution
    extends AbstractDemandModelDistribution<RangeDistributionItem>
    implements RangeDistributionIfc {

  private static final long serialVersionUID = 1L;

  public RangeDistribution() {
    super();
  }

	@Override
	public boolean hasItems() {
		return !isEmpty() && items().map(RangeDistributionItem::amount).anyMatch(amount -> 0 < amount);
	}

  @Override
  public boolean hasItem(int value) {
    return items().anyMatch(item -> item.matches(value));
  }

  @Override
  public RangeDistributionItem getItem(int value) {
    if (0 > value) {
      throw warn(new IllegalArgumentException("Value must be above 0, but was: " + value), log);
    }
    
    if (!hasItem(value)) {
    	addItem(new RangeDistributionItem(value, 0));
    }

    return getItemsInternal()
        .stream()
        .sequential()
        .filter(item -> item.matches(value))
        .findFirst()
        .orElseThrow(() -> warn(new NoSuchElementException("No element found for value: " + value), log));
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

  @Override
  public int amount(int value) {
    return getItem(value).amount();
  }

}
