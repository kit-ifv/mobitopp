package edu.kit.ifv.mobitopp.populationsynthesis;


public interface EconomicalStatusCalculator {

	EconomicalStatus calculateFor(int nominalSize, int numberOfMinors, int income);

}
