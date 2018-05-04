package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Random;

import edu.kit.ifv.mobitopp.data.Zone;

public class AllowChargingProbabilityBased implements ChargePrivatelySelector {

	private Random random;

	public AllowChargingProbabilityBased(long seed) {
		random = new Random(seed);
	}

	@Override
	public boolean canChargeAt(Zone zone) {
		double nextRandom = nextRandom();
		double probability = zone.charging().privateChargingProbability();
		return nextRandom < probability;
	}

	double nextRandom() {
		return random.nextDouble();
	}

}
