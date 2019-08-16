package edu.kit.ifv.mobitopp.simulation;

import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;


public class FilterFractionOfHouseholds implements Predicate<HouseholdForSetup> {

	private final double fractionOfPopulation;
	private double remainder;

	public FilterFractionOfHouseholds(double fractionOfPopulation) {
		this.fractionOfPopulation = fractionOfPopulation;
	}

	@Override
	public boolean test(HouseholdForSetup household) {
		remainder += fractionOfPopulation;
		if (remainder >= 1.0f) {
			remainder -= Math.floor(remainder);
			return true;
		}
		return false;
	}

}
