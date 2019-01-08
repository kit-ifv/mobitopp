package edu.kit.ifv.mobitopp.data.demand;

public class IncomeDistribution extends ContinuousDistribution
    implements ContinuousDistributionIfc {

  private static final long serialVersionUID = 1L;

  public static IncomeDistribution createDefault() {
    return new IncomeDistribution();
  }

}
