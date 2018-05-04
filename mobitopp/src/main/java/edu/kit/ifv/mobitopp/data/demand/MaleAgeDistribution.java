package edu.kit.ifv.mobitopp.data.demand;

import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem.Type;

public class MaleAgeDistribution extends AbstractAgeDistribution {

	private static final long serialVersionUID = 1L;

	public MaleAgeDistribution() {
		super();
	}

	public static MaleAgeDistribution createDefault() {
		MaleAgeDistribution distribution = new MaleAgeDistribution();
		distribution.addItem(new AgeDistributionItem(Type.OVER, 0, 0));

		return distribution;
	}
}
