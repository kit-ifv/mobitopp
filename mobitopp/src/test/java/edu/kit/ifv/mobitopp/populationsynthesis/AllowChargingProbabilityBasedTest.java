package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;

public class AllowChargingProbabilityBasedTest {

	private static final int seed = 0;
	private static final double fiftyFifty = 0.5d;
	private static final double lowerProbability = 0.25d;
	private static final double higherProbability = 0.75d;
	
	private Zone zone;
	private ChargingDataForZone chargingData;

	@Before
	public void initialise() throws Exception {
		zone = mock(Zone.class);
		chargingData = mock(ChargingDataForZone.class);
		when(zone.charging()).thenReturn(chargingData);
		when(chargingData.privateChargingProbability()).thenReturn(fiftyFifty);
	}

	@Test
	public void canChargeWhenProbabilityIsHighEnough() throws Exception {
		boolean canCharge = selector(lowerProbability).canChargeAt(zone);
		
		assertTrue(canCharge);
	}

	@Test
	public void cannotChargeWhenProbabilityIsTooLow() throws Exception {
		boolean cannotCharge = selector(higherProbability).canChargeAt(zone);
		
		assertFalse(cannotCharge);
	}

	private AllowChargingProbabilityBased selector(double nextRandom) {
		return new AllowChargingProbabilityBased(seed) {
			@Override
			double nextRandom() {
				return nextRandom;
			}
		};
	}
}
