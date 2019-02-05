package edu.kit.ifv.mobitopp.data.demand;

import java.util.Objects;

public class ContinuousDistributionItem
    implements DemandModelDistributionItemIfc, Comparable<ContinuousDistributionItem> {

  private static final long serialVersionUID = 1L;

  private final int lowerBound;
  private final int upperBound;
  private int amount;

  public ContinuousDistributionItem(int lowerBound, int upperBound, int amount) {
    super();
    verifyBound(lowerBound);
    verifyBound(upperBound);
    verifyAmount(amount);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.amount = amount;
  }

  public ContinuousDistributionItem(ContinuousDistributionItem other) {
    this(other.lowerBound, other.upperBound, other.amount);
  }

  private void verifyAmount(int amount) {
    if (0 > amount) {
      throw new IllegalArgumentException("Amount is too low. Amount  must be greater than 0 but is " + amount);
    }
  }

  private void verifyBound(int bound) {
    if (0 > bound) {
      throw new IllegalArgumentException("Bound is too low. Bound must be greater than 0 but is  " + bound);
    }
  }

  public boolean matches(int age) {
    return lowerBound <= age && upperBound >= age;
  }

  public int lowerBound() {
    return this.lowerBound;
  }

  public int upperBound() {
    return this.upperBound;
  }

  public int amount() {
    return this.amount;
  }

  public void increment() {
    this.amount += 1;
  }

  public ContinuousDistributionItem createEmpty() {
    return new ContinuousDistributionItem(lowerBound, upperBound, 0);
  }

  public int compareTo(ContinuousDistributionItem other) {
    if (equals(other)) {
      return 0;
    }

    if (lowerBound < other.lowerBound) {
      return -1;
    }
    if (upperBound < other.upperBound) {
      return -1;
    }
    if (upperBound > other.upperBound) {
      return 1;
    }
    return amount - other.amount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, lowerBound, upperBound);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ContinuousDistributionItem other = (ContinuousDistributionItem) obj;
    return amount == other.amount && lowerBound == other.lowerBound
        && upperBound == other.upperBound;
  }

  @Override
  public String toString() {
    return lowerBound + "-" + upperBound + ":" + amount;
  }

}
