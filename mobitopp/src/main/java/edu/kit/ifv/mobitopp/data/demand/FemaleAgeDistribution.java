package edu.kit.ifv.mobitopp.data.demand;

import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem.Type;

public class FemaleAgeDistribution extends AbstractAgeDistribution {

	private static final long serialVersionUID = 1L;

	public FemaleAgeDistribution() {
		super();
	}

	public static FemaleAgeDistribution createDefault() {
		FemaleAgeDistribution distribution = new FemaleAgeDistribution();
		distribution.addItem(new AgeDistributionItem(Type.OVER, 0, 0));

		return distribution;
	}
}
