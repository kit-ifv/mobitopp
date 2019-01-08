package edu.kit.ifv.mobitopp.data.demand;

public class DefaultDistributions {

  public MaleAgeDistribution createMaleAge() {
    MaleAgeDistribution distribution = new MaleAgeDistribution();
    distribution.addItem(new ContinuousDistributionItem(0, Integer.MAX_VALUE, 0));
    return distribution;
  }

  public FemaleAgeDistribution createFemaleAge() {
    FemaleAgeDistribution distribution = new FemaleAgeDistribution();
    distribution.addItem(new ContinuousDistributionItem(0, Integer.MAX_VALUE, 0));
    return distribution;
  }
  
  public IncomeDistribution createIncome() {
    return new IncomeDistribution();
  }
}
