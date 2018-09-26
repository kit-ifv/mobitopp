package edu.kit.ifv.mobitopp.data.demand;

public class MaleAgeDistribution extends AbstractAgeDistribution {

	private static final long serialVersionUID = 1L;

	public MaleAgeDistribution() {
		super();
	}

	public static MaleAgeDistribution createDefault() {
		MaleAgeDistribution distribution = new MaleAgeDistribution();
		distribution.addItem(new AgeDistributionItem(0, Integer.MAX_VALUE, 0));
		return distribution;
	}
}
