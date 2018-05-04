package edu.kit.ifv.mobitopp.simulation.emobility;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ChargingPowerTest {

	@Test
	public void zeroChargingPower() throws Exception {
		double power = ChargingPower.zero.inKw();

		assertThat(power, is(closeTo(0.0, 1e-6)));
	}

	@Test
	public void createFromWatt() {
		int watt = 22_000;
		double kiloWatt = watt / 1000.0;
		ChargingPower inWatt = new ChargingPower(watt);
		ChargingPower inKiloWatt = ChargingPower.fromKw(kiloWatt);

		assertThat(inKiloWatt, is(equalTo(inWatt)));
	}

}
