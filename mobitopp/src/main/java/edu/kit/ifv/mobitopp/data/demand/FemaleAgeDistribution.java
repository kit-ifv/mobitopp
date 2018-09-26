package edu.kit.ifv.mobitopp.data.demand;

public class FemaleAgeDistribution extends AbstractAgeDistribution {

	private static final long serialVersionUID = 1L;

	public FemaleAgeDistribution() {
		super();
	}

	public static FemaleAgeDistribution createDefault() {
		FemaleAgeDistribution distribution = new FemaleAgeDistribution();
		distribution.addItem(new AgeDistributionItem(0, Integer.MAX_VALUE, 0));
		return distribution;
	}
}
