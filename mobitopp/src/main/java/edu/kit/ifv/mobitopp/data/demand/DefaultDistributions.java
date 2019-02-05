package edu.kit.ifv.mobitopp.data.demand;

public class DefaultDistributions {

  public ContinuousDistributionIfc createMaleAge() {
    ContinuousDistributionIfc distribution = new ContinuousDistribution();
    distribution.addItem(new ContinuousDistributionItem(0, Integer.MAX_VALUE, 0));
    return distribution;
  }

  public ContinuousDistributionIfc createFemaleAge() {
    ContinuousDistributionIfc distribution = new ContinuousDistribution();
    distribution.addItem(new ContinuousDistributionItem(0, Integer.MAX_VALUE, 0));
    return distribution;
  }
  
  public ContinuousDistributionIfc createIncome() {
    return new ContinuousDistribution();
  }
}
