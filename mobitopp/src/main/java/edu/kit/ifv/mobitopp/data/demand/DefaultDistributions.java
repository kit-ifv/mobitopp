package edu.kit.ifv.mobitopp.data.demand;

public class DefaultDistributions {

  public RangeDistributionIfc createMaleAge() {
    RangeDistributionIfc distribution = new RangeDistribution();
    distribution.addItem(new RangeDistributionItem(0, Integer.MAX_VALUE, 0));
    return distribution;
  }

  public RangeDistributionIfc createFemaleAge() {
    RangeDistributionIfc distribution = new RangeDistribution();
    distribution.addItem(new RangeDistributionItem(0, Integer.MAX_VALUE, 0));
    return distribution;
  }
  
  public RangeDistributionIfc createIncome() {
    return new RangeDistribution();
  }
}
