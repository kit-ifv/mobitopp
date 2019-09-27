package edu.kit.ifv.mobitopp.populationsynthesis;


public class EconomicalStatusCalculators {

	public static class MiD2017 implements EconomicalStatusCalculator {

		@Override
		public EconomicalStatus calculateFor(int nominalSize, int income) {
			return EconomicalStatus.veryLow;
		}
			
	}

	public static EconomicalStatusCalculator mid2017() {
		return new MiD2017();
	}
}
